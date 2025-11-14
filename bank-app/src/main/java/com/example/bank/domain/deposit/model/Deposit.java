package com.example.bank.domain.deposit.model;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.customer.model.CustomerProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "deposits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerProfile customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "principal_amount", nullable = false)
    private double principalAmount;


    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "monthly_interest", nullable = false)
    private double monthlyInterest;

    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Column(nullable = false, length = 20)
    private DepositStatus status;

    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;
}