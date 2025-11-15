package com.example.bank.domain.bonus.service;

import com.example.bank.domain.bonus.decorator.*;
import com.example.bank.domain.bonus.model.BonusAccount;
import com.example.bank.domain.bonus.repository.BonusAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BonusAccountService {
    private final BonusAccountRepository bonusAccountRepository;
    private final Map<String, CashbackCalculator> calculators = new HashMap<>();

    @Transactional
    public BonusAccount createBonusAccount(Long customerId) {
        BonusAccount account = BonusAccount.builder()
                .customerId(customerId)
                .balance(BigDecimal.ZERO)
                .createdAt(java.time.OffsetDateTime.now())
                .build();
        account = bonusAccountRepository.save(account);
        return account;
    }

    public BonusAccount getBonusAccount(Long customerId) {
        return bonusAccountRepository.findByCustomerId(customerId);
    }

    public void addGroceryBonus() {
        calculators.put("GROCERY", new GroceryCashbackDecorator(calculators.getOrDefault("GROCERY", new BaseCashbackCalculator())));
    }

    public void addFuelBonus() {
        calculators.put("FUEL", new FuelCashbackDecorator(calculators.getOrDefault("FUEL", new BaseCashbackCalculator())));
    }

    public void addEntertainmentBonus() {
        calculators.put("ENTERTAINMENT", new EntertainmentCashbackDecorator(calculators.getOrDefault("ENTERTAINMENT", new BaseCashbackCalculator())));
    }

    public void addTravelBonus() {
        calculators.put("TRAVEL", new TravelCashbackDecorator(calculators.getOrDefault("TRAVEL", new BaseCashbackCalculator())));
    }

    public void addHealthBonus() {
        calculators.put("HEALTH", new HealthCashbackDecorator(calculators.getOrDefault("HEALTH", new BaseCashbackCalculator())));
    }

    public void addEducationBonus() {
        calculators.put("EDUCATION", new EducationCashbackDecorator(calculators.getOrDefault("EDUCATION", new BaseCashbackCalculator())));
    }

    public void addOtherBonus() {
        calculators.put("OTHER", new OtherCashbackDecorator(calculators.getOrDefault("OTHER", new BaseCashbackCalculator())));
    }

    public BigDecimal calculateAndApplyCashback(Long customerId, BigDecimal amount, String category) {
        CashbackCalculator calculator = calculators.get(category);
        if (calculator == null) {
            calculator = new BaseCashbackCalculator();
        }
        BigDecimal cashback = calculator.calculateCashback(amount);
        BonusAccount account = bonusAccountRepository.findByCustomerId(customerId);
        if (account != null) {
            account.setBalance(account.getBalance().add(cashback));
            bonusAccountRepository.save(account);
        }
        calculators.remove(category);
        return cashback;
    }
}
