package com.example.bank.domain.deposit.service;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.deposit.model.DepositStatus;
import com.example.bank.domain.deposit.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Deposit createDeposit(Long accountId, double principalAmount, String currency, double monthlyInterest, int termMonths) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        if (account.getBalance().compareTo(BigDecimal.valueOf(principalAmount)) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account: " + accountId);
        }

        account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(principalAmount)));
        accountRepository.save(account);

        Deposit deposit = Deposit.builder()
                .customer(account.getCustomer())
                .account(account)
                .principalAmount(principalAmount)
                .currency(currency)
                .monthlyInterest(monthlyInterest)
                .termMonths(termMonths)
                .status(DepositStatus.ACTIVE)
                .openedAt(OffsetDateTime.now())
                .build();

        return depositRepository.save(deposit);
    }

    @Transactional(readOnly = true)
    public List<Deposit> getDepositsByCustomer(CustomerProfile customer) {
        return depositRepository.findByCustomerOrderByOpenedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public Optional<Deposit> getDeposit(Long depositId) {
        return depositRepository.findById(depositId);
    }

    @Transactional
    public Deposit closeDeposit(Long depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found: " + depositId));

        if (deposit.getStatus() != DepositStatus.ACTIVE) {
            throw new IllegalArgumentException("Deposit is not active: " + depositId);
        }

        double interest = deposit.getPrincipalAmount() * deposit.getMonthlyInterest() * deposit.getTermMonths() / 12.0;
        double totalAmount = deposit.getPrincipalAmount() + interest;

        Account account = deposit.getAccount();
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(totalAmount)));
        accountRepository.save(account);

        deposit.setStatus(DepositStatus.CLOSED);
        deposit.setClosedAt(OffsetDateTime.now());

        return depositRepository.save(deposit);
    }

    @Transactional(readOnly = true)
    public List<Deposit> getDepositsByStatus(DepositStatus status) {
        return depositRepository.findByStatus(status);
    }
}
