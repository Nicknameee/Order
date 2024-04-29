package io.management.ua.utility.api.datatrans.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.management.ua.transactions.dto.TransactionInitiativeDTO;
import io.management.ua.utility.api.datatrans.configuration.DataTransConfiguration;
import io.management.ua.utility.api.datatrans.models.authorization.AuthorizationBody;
import io.management.ua.utility.api.datatrans.models.authorization.AuthorizationRequest;
import io.management.ua.utility.api.datatrans.models.authorization.TransactionAuthorizationAPIModel;
import io.management.ua.utility.api.datatrans.models.authorization.TransactionAuthorizationXmlModel;
import io.management.ua.utility.api.datatrans.models.payment.PaymentBody;
import io.management.ua.utility.api.datatrans.models.payment.PaymentRequest;
import io.management.ua.utility.api.datatrans.models.payment.TransactionPaymentAPIModel;
import io.management.ua.utility.api.datatrans.models.payment.TransactionPaymentXmlModel;
import io.management.ua.utility.models.NetworkResponse;
import io.management.ua.utility.network.NetworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataTransTransactionService {
    private final NetworkService networkService;
    private final DataTransConfiguration dataTransConfiguration;

    public TransactionAuthorizationAPIModel processTransaction(TransactionInitiativeDTO transactionInitiativeDTO) {
        XmlMapper xmlMapper = new XmlMapper();

        try {
            TransactionAuthorizationAPIModel authorizationServiceModel = getTransactionAuthorizationAPIModel(transactionInitiativeDTO);
            String xml = xmlMapper.writeValueAsString(authorizationServiceModel);
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
            headers.put(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", dataTransConfiguration.getMerchantId(), dataTransConfiguration.getPassword()).getBytes(StandardCharsets.UTF_8)));

            NetworkResponse networkResponse = networkService.performRequest(HttpMethod.POST, dataTransConfiguration.getAuthorization().getApiUrl(), headers, xml);

            TransactionAuthorizationAPIModel response = xmlMapper.readValue((String) networkResponse.getBody(), TransactionAuthorizationAPIModel.class);

            if (networkResponse.getHttpStatus() != HttpStatus.OK) {
                log.error("Error occurred while performing a request, http status {}, body {}", networkResponse.getHttpStatus(), response.getAuthorizationBody());
            }

            return response;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public TransactionPaymentAPIModel settleTransaction(TransactionAuthorizationAPIModel transactionAuthorizationAPIModel) {
        XmlMapper xmlMapper = new XmlMapper();

        try {
            TransactionPaymentAPIModel paymentServiceModel = getTransactionPaymentAPiModel(transactionAuthorizationAPIModel);
            String xml = xmlMapper.writeValueAsString(paymentServiceModel);
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
            headers.put(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", dataTransConfiguration.getMerchantId(), dataTransConfiguration.getPassword()).getBytes(StandardCharsets.UTF_8)));

            NetworkResponse networkResponse = networkService.performRequest(HttpMethod.POST, dataTransConfiguration.getPayment().getApiUrl(), headers, xml);

            TransactionPaymentAPIModel response = xmlMapper.readValue((String) networkResponse.getBody(), TransactionPaymentAPIModel.class);

            if (networkResponse.getHttpStatus() != HttpStatus.OK) {
                log.error("Error occurred while performing a request, http status {}, body {}", networkResponse.getHttpStatus(), response.getPaymentBody());
            }

            return response;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private TransactionAuthorizationAPIModel getTransactionAuthorizationAPIModel(TransactionInitiativeDTO transactionInitiativeDTO) throws JsonProcessingException {
        TransactionAuthorizationAPIModel authorizationServiceModel = new TransactionAuthorizationAPIModel();
        authorizationServiceModel.setVersion(dataTransConfiguration.getAuthorization().getApiVersion());

        AuthorizationBody authorizationBody = new AuthorizationBody();
        authorizationBody.setMerchantId(dataTransConfiguration.getMerchantId());

        TransactionAuthorizationXmlModel transactionAuthorizationXmlModel = getTransactionAuthorizationXmlModel(transactionInitiativeDTO);

        authorizationBody.setTransaction(transactionAuthorizationXmlModel);

        authorizationServiceModel.setAuthorizationBody(authorizationBody);

        return authorizationServiceModel;
    }

    private TransactionAuthorizationXmlModel getTransactionAuthorizationXmlModel(TransactionInitiativeDTO transactionInitiativeDTO) {
        TransactionAuthorizationXmlModel transactionAuthorizationXmlModel = new TransactionAuthorizationXmlModel();
        transactionAuthorizationXmlModel.setRefNumber(String.valueOf(transactionInitiativeDTO.getReference()));

        AuthorizationRequest authorizationRequest = new AuthorizationRequest();

        authorizationRequest.setGooglePayData(transactionInitiativeDTO.getPaymentToken());
        authorizationRequest.setSign(dataTransConfiguration.getSign());
        authorizationRequest.setAmount(transactionInitiativeDTO.getAmount());
        authorizationRequest.setCurrency(dataTransConfiguration.getAcquiringCurrency());

        transactionAuthorizationXmlModel.setAuthorizationRequest(authorizationRequest);

        return transactionAuthorizationXmlModel;
    }

    private TransactionPaymentAPIModel getTransactionPaymentAPiModel(TransactionAuthorizationAPIModel transactionAuthorizationAPIModel) {
        TransactionPaymentAPIModel transactionPaymentAPIModel = new TransactionPaymentAPIModel();
        transactionPaymentAPIModel.setVersion(dataTransConfiguration.getPayment().getApiVersion());

        PaymentBody paymentBody = new PaymentBody();
        paymentBody.setMerchantId(dataTransConfiguration.getMerchantId());
        paymentBody.setTransaction(getTransactionPaymentXmlModel(transactionAuthorizationAPIModel));

        transactionPaymentAPIModel.setPaymentBody(paymentBody);

        return transactionPaymentAPIModel;
    }

    private TransactionPaymentXmlModel getTransactionPaymentXmlModel(TransactionAuthorizationAPIModel transactionAuthorizationAPIModel) {
        TransactionPaymentXmlModel transactionPaymentXmlModel = new TransactionPaymentXmlModel();
        transactionPaymentXmlModel.setRefNumber(transactionAuthorizationAPIModel.getAuthorizationBody().getTransaction().getRefNumber());

        TransactionAuthorizationXmlModel transactionAuthorizationXmlModel = transactionAuthorizationAPIModel.getAuthorizationBody().getTransaction();

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setTransactionId(transactionAuthorizationXmlModel.getAuthorizationResponse().getUppTransactionId());
        paymentRequest.setAmount(transactionAuthorizationAPIModel.getAuthorizationBody().getTransaction().getAuthorizationRequest().getAmount());
        paymentRequest.setCurrency(transactionAuthorizationAPIModel.getAuthorizationBody().getTransaction().getAuthorizationRequest().getCurrency());

        transactionPaymentXmlModel.setPaymentRequest(paymentRequest);

        return transactionPaymentXmlModel;
    }
}
