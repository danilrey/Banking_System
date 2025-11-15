package com.example.bank.domain.credit.service;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.CreditStatus;
import com.example.bank.domain.credit.model.CreditType;
import com.example.bank.domain.credit.repository.CreditRepository;
import com.example.bank.domain.credit.strategy.CreditStrategy;
import com.example.bank.domain.credit.strategy.CreditStrategyFactory;
import com.example.bank.domain.currency.model.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditStrategyFactory strategyFactory;

    @Transactional
    public Credit createCredit(CustomerProfile customer, BigDecimal principalAmount, Currency currency, BigDecimal interestRateAnnual, int termMonths, CreditType creditType) {
        CreditStrategy strategy = strategyFactory.getStrategy(creditType);

        // Валидация ставки
        if (interestRateAnnual.compareTo(strategy.getMinInterestRate()) < 0) {
            throw new IllegalArgumentException("Interest rate too low for " + creditType + ". Minimum: " + strategy.getMinInterestRate());
        }

        // Валидация суммы
        if (principalAmount.compareTo(strategy.getMaxAmount()) > 0) {
            throw new IllegalArgumentException("Amount too high for " + creditType + ". Maximum: " + strategy.getMaxAmount());
        }

        // Валидация срока
        if (termMonths > strategy.getMaxTermMonths()) {
            throw new IllegalArgumentException("Term too long for " + creditType + ". Maximum months: " + strategy.getMaxTermMonths());
        }

        Credit credit = Credit.builder()
                .customer(customer)
                .principalAmount(principalAmount)
                .currency(currency)
                .interestRateAnnual(interestRateAnnual)
                .termMonths(termMonths)
                .creditType(creditType)
                .status(CreditStatus.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .build();

        return creditRepository.save(credit);
    }

    @Transactional(readOnly = true)
    public Optional<Credit> getCredit(Long creditId) {
        return creditRepository.findById(creditId);
    }

    @Transactional
    public Credit closeCredit(Long creditId) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Credit not found: " + creditId));

        if (credit.getStatus() != CreditStatus.ACTIVE) {
            throw new IllegalArgumentException("Credit is not active: " + creditId);
        }

        BigDecimal interest = credit.getPrincipalAmount().multiply(credit.getInterestRateAnnual()).multiply(BigDecimal.valueOf(credit.getTermMonths())).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = credit.getPrincipalAmount().add(interest);

        credit.setStatus(CreditStatus.CLOSED);
        credit.setClosedAt(OffsetDateTime.now());

        return creditRepository.save(credit);
    }

    @Transactional(readOnly = true)
    public List<Credit> getCreditsByStatus(CreditStatus status) {
        return creditRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Credit> getCreditsByCustomer(CustomerProfile customer) {
        return creditRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public List<Credit> getAllCredits() {
        return creditRepository.findAll();
    }
}
