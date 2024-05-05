package io.management.ua.transactions.service;

import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.annotations.DefaultStringValue;
import io.management.ua.products.attributes.Currency;
import io.management.ua.transactions.dto.TransactionFilter;
import io.management.ua.transactions.dto.TransactionInitiativeDTO;
import io.management.ua.transactions.dto.TransactionStateMessage;
import io.management.ua.transactions.entity.Transaction;
import io.management.ua.transactions.mapper.TransactionMapper;
import io.management.ua.transactions.repository.TransactionRepository;
import io.management.ua.utility.TimeUtil;
import io.management.ua.utility.api.datatrans.configuration.DataTransConfiguration;
import io.management.ua.utility.api.datatrans.models.authorization.AuthorizationResponse;
import io.management.ua.utility.api.datatrans.models.authorization.TransactionAuthorizationAPIModel;
import io.management.ua.utility.api.datatrans.models.TransactionProcessingError;
import io.management.ua.utility.api.datatrans.models.payment.TransactionPaymentAPIModel;
import io.management.ua.utility.api.datatrans.service.DataTransTransactionService;
import io.management.ua.utility.enums.WebSocketTopics;
import io.management.ua.utility.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransactionService {
    @PersistenceContext(unitName = "database")
    private final EntityManager entityManager;
    private final DataTransTransactionService dataTransTransactionService;
    private final TransactionRepository transactionRepository;
    private final DataTransConfiguration dataTransConfiguration;
    private final WebSocketService webSocketService;
    private final TransactionMapper transactionMapper;

    public List<Transaction> getTransactions(@Nullable @Valid TransactionFilter transactionFilter,
                                             @DefaultNumberValue Integer page,
                                             @DefaultNumberValue(number = 100) Integer size,
                                             @DefaultStringValue(string = "issuedAt") String sortBy,
                                             @DefaultStringValue(string = "ASC") String direction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        if (transactionFilter != null) {
            if (transactionFilter.getIssuedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction.Fields.issuedAt), transactionFilter.getIssuedAtFrom()));
            }
            if (transactionFilter.getIssuedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Transaction.Fields.issuedAt), transactionFilter.getIssuedAtTo()));
            }
        }

        query.where(predicates.toArray(Predicate[]::new));

        if (direction != null) {
            switch (Sort.Direction.valueOf(direction)) {
                case DESC -> query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
                case ASC -> query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Transaction.Fields.issuedAt)));
        }

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    private Transaction processIncomingTransactionData(TransactionInitiativeDTO transactionInitiative) {
        Transaction transaction = new Transaction();

        transaction.setId(UUID.randomUUID());
        transaction.setCustomerId(transactionInitiative.getCustomerId());
        transaction.setAmount(transactionInitiative.getAmount());
        transaction.setSourceCurrency(transactionInitiative.getCurrency());
        transaction.setAcquiringCurrency(Currency.valueOf(dataTransConfiguration.getAcquiringCurrency()));
        transaction.setReference(transactionInitiative.getReference());
        transaction.setStatus("CREATED");
        transaction.setIssuedAt(TimeUtil.getCurrentDateTime());
        transaction.setMerchantId(dataTransConfiguration.getMerchantId());

        return transaction;
    }

    @Transactional
    public Transaction processIncomingTransaction(@Valid TransactionInitiativeDTO transactionInitiativeDTO) {
        Transaction transaction = processIncomingTransactionData(transactionInitiativeDTO);

        TransactionAuthorizationAPIModel transactionAuthorizationAPIModel = dataTransTransactionService.processTransaction(transactionInitiativeDTO);
        TransactionStateMessage transactionStateMessage;

        if ((!transactionAuthorizationAPIModel.getAuthorizationBody().getStatus().toLowerCase().equals("error")
                && transactionAuthorizationAPIModel.getError() == null)
                || (transactionAuthorizationAPIModel.getError() != null && transactionAuthorizationAPIModel.getError().getErrorMessage() == null)) {
            AuthorizationResponse authorizationResponse = transactionAuthorizationAPIModel.getAuthorizationBody().getTransaction().getAuthorizationResponse();
            transaction.setStatus(transactionAuthorizationAPIModel.getAuthorizationBody().getStatus().toUpperCase());
            transaction.setTransactionId(authorizationResponse.getUppTransactionId());
            transaction.setAuthorizationCode(authorizationResponse.getAuthorizationCode());
            transaction.setAcquireAuthorizationCode(authorizationResponse.getAcqAuthorizationCode());
            transaction.setTransactionProcessingCountry(authorizationResponse.getReturnCustomerCountry());
            transaction.setTransactionType(transactionAuthorizationAPIModel.getAuthorizationBody().getTransaction().getAuthorizationRequest().getRequestType());
            transaction.setTransactionAlias(authorizationResponse.getAliasCC());
            transaction.setExpiration(String.format("%s/%s", authorizationResponse.getExpirationMonth(), authorizationResponse.getExpirationYear()));
            transaction.setPan(authorizationResponse.getPan());

            transactionStateMessage = transactionMapper.modelToStateMessage(transaction);
            transactionStateMessage.setAuthorized(true);

            TransactionPaymentAPIModel transactionPaymentAPIModel = dataTransTransactionService.settleTransaction(transactionAuthorizationAPIModel);

            if (!transactionAuthorizationAPIModel.getAuthorizationBody().getStatus().equals("ERROR")
                    && transactionAuthorizationAPIModel.getError() == null
                    || transactionAuthorizationAPIModel.getError().getErrorMessage() == null) {
                if (transactionPaymentAPIModel.getError() == null || transactionPaymentAPIModel.getError().getErrorMessage() == null) {
                    transaction.setSettledAt(TimeUtil.getCurrentDateTime());
                    transactionStateMessage.setSettled(true);
                } else {
                    TransactionProcessingError transactionProcessingError = transactionPaymentAPIModel.getError();
                    transactionStateMessage.setSettled(false);
                    log.error(transactionProcessingError.getErrorMessage(), transactionProcessingError);
                }
            }
        } else {
            TransactionProcessingError transactionProcessingError = transactionAuthorizationAPIModel.getError();
            if (transactionProcessingError != null) {
                log.error(transactionProcessingError.getErrorMessage(), transactionProcessingError);
            }

            transactionStateMessage = transactionMapper.modelToStateMessage(transaction);
            transactionStateMessage.setAuthorized(false);
        }

        webSocketService.sendMessage(WebSocketTopics.TRANSACTION_STATE.getTopic() + "/" + transactionStateMessage.getCustomerId(), transactionStateMessage);

        return transactionRepository.saveAndFlush(transaction);
    }
}
