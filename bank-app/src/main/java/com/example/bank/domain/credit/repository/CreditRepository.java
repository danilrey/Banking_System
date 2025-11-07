package com.example.bank.domain.credit.repository;

import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.customer.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findByCustomer(CustomerProfile customer);

    List<Credit> findByCustomerAndStatus(CustomerProfile customer, String status);
}
