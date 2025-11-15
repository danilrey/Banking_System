package com.example.bank.domain.credit.strategy;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.CreditType;
import com.example.bank.domain.credit.service.CreditService;
import com.example.bank.domain.currency.model.Currency;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumerCreditStrategy implements CreditStrategy {

    private final CreditService creditService;

    @Override
    public BigDecimal getMinInterestRate() {
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

    public Credit createCreditForAccount(Account account,
                                         double amount,
                                         String currency,
                                         double interestRate,
                                         int termMonths) {
        return creditService.createCredit(
                account.getCustomer(),
                BigDecimal.valueOf(amount),
                Currency.valueOf(currency),
                BigDecimal.valueOf(interestRate),
                termMonths,
                CreditType.PERSONAL
        );
    }
}
