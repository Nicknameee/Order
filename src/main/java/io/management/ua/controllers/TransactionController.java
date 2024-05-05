package io.management.ua.controllers;

import io.management.ua.response.Response;
import io.management.ua.transactions.dto.TransactionFilter;
import io.management.ua.transactions.dto.TransactionInitiativeDTO;
import io.management.ua.transactions.dto.TransactionManualInitiativeModel;
import io.management.ua.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/transactions")
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public Response<?> getTransactions(@RequestBody(required = false) TransactionFilter transactionFilter,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size,
                                       @RequestParam(required = false) String sortBy,
                                       @RequestParam(required = false) String direction) {
        return Response.ok(transactionService.getTransactions(transactionFilter, page, size, sortBy, direction));
    }

    @PostMapping("/initiate")
    public Response<?> initiateTransaction(@RequestBody TransactionInitiativeDTO transactionInitiativeDTO) {
        return Response.ok(transactionService.processIncomingTransaction(transactionInitiativeDTO));
    }

    @PreAuthorize("hasAuthority(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PostMapping("/manual/payment")
    public Response<?> addManualPayment(@RequestBody TransactionManualInitiativeModel transactionManualInitiativeModel) {
        return Response.ok(transactionService.addManualPayment(transactionManualInitiativeModel));
    }
}
