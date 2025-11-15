package com.example.bank.domain.bonus.decorator;

import java.math.BigDecimal;

public class TravelCashbackDecorator implements CashbackCalculator {
    private final CashbackCalculator wrapped;

    public TravelCashbackDecorator(CashbackCalculator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public BigDecimal calculateCashback(BigDecimal amount) {
        BigDecimal base = wrapped.calculateCashback(amount);
        base = base.add(amount.multiply(BigDecimal.valueOf(0.025)));
        return base;
    }
}
