package com.example.bank.domain.credit.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ConsumerCreditStrategy implements CreditStrategy {

    @Override
    public BigDecimal getMinInterestRate() {
        // например 5% годовых
        return BigDecimal.valueOf(5.0);
    }

    @Override
    public BigDecimal getMaxAmount() {
        return BigDecimal.valueOf(1_000_000);
    }

    @Override
    public int getMaxTermMonths() {
        return 60;
    }
}
