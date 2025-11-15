package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.currency.model.Currency;
import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.deposit.model.DepositStatus;
import com.example.bank.domain.deposit.service.DepositService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/api/deposits")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public ResponseEntity<DepositResponse> createDeposit(@RequestBody CreateDepositRequest request) {
        Deposit deposit = depositService.createDeposit(request.getAccountId(), request.getPrincipalAmount(), request.getCurrency(), request.getMonthlyInterest(), request.getTermMonths());
        DepositResponse response = mapToResponse(deposit);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/api/deposits")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public ResponseEntity<List<DepositResponse>> getDeposits(@AuthenticationPrincipal CustomerProfile customer) {
        List<Deposit> deposits = depositService.getAllDeposits();
        List<DepositResponse> responses = deposits.stream().map(this::mapToResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/deposits/{id}")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public ResponseEntity<DepositResponse> getDeposit(@PathVariable Long id) {
        Optional<Deposit> depositOpt = depositService.getDeposit(id);
        if (depositOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DepositResponse response = mapToResponse(depositOpt.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/deposits/{id}/close")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public ResponseEntity<DepositResponse> closeDeposit(@PathVariable Long id) {
        System.out.println("Closing deposit: " + id);

        Deposit closed = depositService.closeDeposit(id);
        return ResponseEntity.ok(mapToResponse(closed));
    }

    @GetMapping("/api/deposits/status/{status}")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public ResponseEntity<List<DepositResponse>> getDepositsByStatus(@PathVariable DepositStatus status) {
        List<Deposit> deposits = depositService.getDepositsByStatus(status);
        List<DepositResponse> responses = deposits.stream().map(this::mapToResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ui/deposits")
    public String getDepositsPage(Model model, @AuthenticationPrincipal CustomerProfile customer) {
        List<Deposit> deposits = depositService.getAllDeposits();
        model.addAttribute("deposits", deposits);
        return "deposits";
    }

    @PostMapping("/ui/deposits")
    public String createDepositPage(@RequestParam Long accountId,
                                    @RequestParam BigDecimal principalAmount,
                                    @RequestParam Currency currency,
                                    @RequestParam BigDecimal monthlyInterest,
                                    @RequestParam int termMonths) {
        depositService.createDeposit(accountId, principalAmount, currency, monthlyInterest, termMonths);
        return "redirect:/ui/deposits";
    }

    @Data
    public static class CreateDepositRequest {
        private Long accountId;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal monthlyInterest;
        private int termMonths;
    }

    @Data
    public static class DepositResponse {
        private Long id;
        private Long accountId;
        private BigDecimal principalAmount;
        private Currency currency;
        private BigDecimal monthlyInterest;
        private int termMonths;
        private DepositStatus status;
        private OffsetDateTime openedAt;
        private OffsetDateTime closedAt;
    }

    private DepositResponse mapToResponse(Deposit deposit) {
        DepositResponse response = new DepositResponse();
        response.setId(deposit.getId());
        response.setAccountId(deposit.getAccount().getId());
        response.setPrincipalAmount(deposit.getPrincipalAmount());
        response.setCurrency(deposit.getCurrency());
        response.setMonthlyInterest(deposit.getMonthlyInterest());
        response.setTermMonths(deposit.getTermMonths());
        response.setStatus(deposit.getStatus());
        response.setOpenedAt(deposit.getOpenedAt());
        response.setClosedAt(deposit.getClosedAt());
        return response;
    }
}