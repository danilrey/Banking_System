package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import com.example.bank.domain.payment.model.Payment;
import com.example.bank.domain.payment.model.PaymentCategory;
import com.example.bank.domain.payment.model.PaymentStatus;
import com.example.bank.domain.payment.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomerProfileRepository customerProfileRepository;

    @PostMapping("/account/{accountId}/pay")
    public ResponseEntity<Payment> payFromAccountNow(@PathVariable Long accountId, @RequestBody PaymentRequest request) {
        PaymentCategory category = PaymentCategory.valueOf(request.getCategory().toUpperCase());

        Payment payment = paymentService.payFromAccountNow(
                accountId,
                request.getAmount(),
                request.getCurrency(),
                category,
                request.getProviderName(),
                request.getDetailsJson()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/account/{accountId}/schedule")
    public ResponseEntity<Payment> scheduleFromAccount(@PathVariable Long accountId, @RequestBody SchedulePaymentRequest request) {
        PaymentCategory category = PaymentCategory.valueOf(request.getCategory().toUpperCase());
        Payment payment = paymentService.scheduleFromAccount(
                accountId,
                request.getAmount(),
                request.getCurrency(),
                category,
                request.getProviderName(),
                request.getDetailsJson(),
                request.getScheduledAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }


    @PostMapping("/card/{cardId}/pay")
    public ResponseEntity<Payment> payFromCardNow(
            @PathVariable Long cardId,
            @RequestBody PaymentRequest request
    ) {
        PaymentCategory category = PaymentCategory.valueOf(request.getCategory().toUpperCase());

        Payment payment = paymentService.payFromCardNow(
                cardId,
                request.getAmount(),
                request.getCurrency(),
                category,
                request.getProviderName(),
                request.getDetailsJson()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }


    @PostMapping("/card/{cardId}/schedule")
    public ResponseEntity<Payment> scheduleFromCard(
            @PathVariable Long cardId,
            @RequestBody SchedulePaymentRequest request
    ) {
        PaymentCategory category = PaymentCategory.valueOf(request.getCategory().toUpperCase());

        Payment payment = paymentService.scheduleFromCard(
                cardId,
                request.getAmount(),
                request.getCurrency(),
                category,
                request.getProviderName(),
                request.getDetailsJson(),
                request.getScheduledAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }



    @PostMapping("/{paymentId}/execute")
    public ResponseEntity<Payment> executeScheduled(@PathVariable Long paymentId) {
        Payment payment = paymentService.executeScheduledPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Payment> cancelScheduled(@PathVariable Long paymentId) {
        Payment payment = paymentService.cancelScheduledPayment(paymentId);
        return ResponseEntity.ok(payment);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getCustomerPayments(@PathVariable Long customerId) {
        CustomerProfile customer = customerProfileRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found " + customerId));
        return ResponseEntity.ok(paymentService.getCustomerPayments(customer));
    }

    @GetMapping("/customer/{customerId}/status/{status}")
    public ResponseEntity<List<Payment>> getCustomerPaymentByStatus(@PathVariable Long customerId, @PathVariable String status) {
        CustomerProfile customer = customerProfileRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found " + customerId));
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(paymentService.getCustomerPaymentsByStatus(customer, paymentStatus));

    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Payment>> getPaymentsByCategory(@PathVariable String category) {
        PaymentCategory cat = PaymentCategory.valueOf(category.toUpperCase());
        return ResponseEntity.ok(paymentService.getPaymentsByCategory(cat));
    }



    @Data
    public static class SchedulePaymentRequest {
        private BigDecimal amount;
        private String currency;
        private String category;
        private String providerName;
        private String detailsJson;
        private OffsetDateTime scheduledAt;
    }

    @Data
    public static class PaymentRequest {
        private BigDecimal amount;
        private String currency;
        private String category;
        private String providerName;
        private String detailsJson;
    }
}