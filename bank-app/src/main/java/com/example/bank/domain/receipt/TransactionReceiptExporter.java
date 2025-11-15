package com.example.bank.domain.receipt;

import com.example.bank.domain.transaction.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class TransactionReceiptExporter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String exportReceipt(Transaction transaction) throws JsonProcessingException {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("transactionId", transaction.getId());
        receipt.put("amount", transaction.getAmount());
        receipt.put("currency", transaction.getCurrency().name());
        receipt.put("direction", transaction.getDirection());
        receipt.put("description", transaction.getDescription());
        receipt.put("createdAt", transaction.getCreatedAt().toString());
        receipt.put("status", transaction.getStatus());
        receipt.put("type", transaction.getType());

        if (transaction.getRelatedCard() != null) {
            receipt.put("relatedCardId", transaction.getRelatedCard().getId());
        }
        if (transaction.getMeta() != null) {
            receipt.put("meta", transaction.getMeta());
        }

        return objectMapper.writeValueAsString(receipt);
    }
}
