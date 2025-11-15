package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.customer.repository.CustomerProfileRepository;
import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.CreditStatus;
import com.example.bank.domain.credit.model.CreditType;
import com.example.bank.domain.credit.service.CreditService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CreditController {

    private final CreditService creditService;
    private final CustomerProfileRepository customerProfileRepository;

    public CreditController(CreditService creditService,
                            CustomerProfileRepository customerProfileRepository) {
        this.creditService = creditService;
        this.customerProfileRepository = customerProfileRepository;
    }


    @PostMapping("/api/credits")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CreditResponse> createCredit(
            @RequestBody CreateCreditRequest request
    ) {
        CustomerProfile customer = customerProfileRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        Credit credit = creditService.createCredit(
                customer,
                request.getPrincipalAmount(),
                request.getCurrency(),
                request.getInterestRateAnnual(),
                request.getTermMonths(),
                request.getCreditType()
        );

        return ResponseEntity.status(201).body(mapToResponse(credit));
    }

    @GetMapping("/api/credits")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<CreditResponse>> getCredits(
            @RequestParam Long customerId
    ) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        List<Credit> credits = creditService.getCreditsByCustomer(customer);
        List<CreditResponse> responses = credits.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/credits/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CreditResponse> getCredit(@PathVariable Long id) {
        Optional<Credit> creditOpt = creditService.getCredit(id);
        return creditOpt
                .map(credit -> ResponseEntity.ok(mapToResponse(credit)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/credits/{id}/close")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<CreditResponse> closeCredit(@PathVariable Long id) {
        Credit closed = creditService.closeCredit(id);
        return ResponseEntity.ok(mapToResponse(closed));
    }

    @GetMapping("/api/credits/status/{status}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<CreditResponse>> getCreditsByStatus(
            @PathVariable CreditStatus status,
            @RequestParam Long customerId
    ) {
        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        List<CreditResponse> responses = creditService.getCreditsByCustomer(customer).stream()
                .filter(credit -> credit.getStatus() == status)
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/ui/credits")
    public String getCreditsPage(
            Model model,
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        List<Credit> credits = creditService.getAllCredits();
        model.addAttribute("credits", credits);
        return "credits";
    }

    @PostMapping("/ui/credits")
    public String createCreditPage(@RequestParam BigDecimal principalAmount,
                                   @RequestParam Currency currency,
                                   @RequestParam BigDecimal interestRateAnnual,
                                   @RequestParam int termMonths,
                                   @RequestParam CreditType creditType,
                                   @RequestParam String phone) {
        CustomerProfile customer = customerProfileRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        creditService.createCredit(customer, principalAmount, currency, interestRateAnnual, termMonths, creditType);
        return "redirect:/ui/credits";
    }


    @Data
    public static class CreateCreditRequest {
        private Long customerId;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal interestRateAnnual;
        private int termMonths;
        private CreditType creditType;
    }

    @Data
    public static class CreditResponse {
        private Long id;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal interestRateAnnual;
        private int termMonths;
        private CreditType creditType;
        private CreditStatus status;
        private OffsetDateTime createdAt;
        private OffsetDateTime closedAt;
    }


    private CreditResponse mapToResponse(Credit credit) {
        CreditResponse response = new CreditResponse();
        response.setId(credit.getId());
        response.setPrincipalAmount(credit.getPrincipalAmount());
        response.setCurrency(credit.getCurrency());
        response.setInterestRateAnnual(credit.getInterestRateAnnual());
        response.setTermMonths(credit.getTermMonths());
        response.setCreditType(credit.getCreditType());
        response.setStatus(credit.getStatus());
        response.setCreatedAt(credit.getCreatedAt());
        response.setClosedAt(credit.getClosedAt());
        return response;
    }
}
