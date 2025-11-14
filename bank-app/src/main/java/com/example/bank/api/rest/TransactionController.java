package com.example.bank.api.rest;

import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.transaction.model.Transaction;
import com.example.bank.domain.transaction.model.TransactionType;
import com.example.bank.domain.transaction.service.TransactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequest request){
        Transaction tx = transactionService.createTransaction(
                request.getAccountId(),
                request.getType(),
                request.getAmount(),
                request.getCurrency(),
                request.getDirection(),
                request.getDescription(),
                null
        );

        return ResponseEntity.ok(tx);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long accountId){
        return ResponseEntity.ok(transactionService.getAccountTransactions(accountId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id){
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    @PostMapping("/{id}/suspicious")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transaction> markAsSuspicious(@PathVariable Long id, @RequestBody MarkSuspiciousRequest request){
        return ResponseEntity.ok(transactionService.markAsSuspicious(id, request.getReason()));
    }

    @Data
    public static class CreateTransactionRequest{
        private Long accountId;
        private TransactionType type;
        private BigDecimal amount;
        private Currency currency; // changed from String to Currency
        private String direction;
        private String description;
    }

    @Data
    public static class MarkSuspiciousRequest{
        private String reason;
    }
}