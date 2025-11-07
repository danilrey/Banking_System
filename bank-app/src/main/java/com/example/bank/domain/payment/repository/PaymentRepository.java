package com.example.bank.domain.payment.repository;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCustomerOrderByCreatedAtDesc(CustomerProfile customer);

    List<Payment> findByCustomerAndStatus(CustomerProfile customer, String status);

    List<Payment> findByCategory(String category);
}
