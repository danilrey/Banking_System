package com.example.bank.domain.bonus.decorator;

import java.math.BigDecimal;

public class HealthCashbackDecorator implements CashbackCalculator {
    private final CashbackCalculator wrapped;

    public HealthCashbackDecorator(CashbackCalculator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public BigDecimal calculateCashback(BigDecimal amount) {
        BigDecimal base = wrapped.calculateCashback(amount);
        base = base.add(amount.multiply(BigDecimal.valueOf(0.02)));
        return base;
    }
}
