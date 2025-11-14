package com.example.bank.api.rest;

import com.example.bank.domain.customer.model.CustomerProfile;
import com.example.bank.domain.deposit.model.Deposit;
import com.example.bank.domain.deposit.model.DepositStatus;
import com.example.bank.domain.deposit.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/api/deposits")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<Deposit> createDeposit(@RequestParam Long accountId,
                                                 @RequestParam double principalAmount,
                                                 @RequestParam String currency,
                                                 @RequestParam double monthlyInterest,
                                                 @RequestParam int termMonths) {
        Deposit deposit = depositService.createDeposit(accountId, principalAmount, currency, monthlyInterest, termMonths);
        return ResponseEntity.status(201).body(deposit);
    }

    @GetMapping("/api/deposits")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<Deposit>> getDeposits(@AuthenticationPrincipal CustomerProfile customer) {
        List<Deposit> deposits = depositService.getDepositsByCustomer(customer);
        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/api/deposits/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<Deposit> getDeposit(@PathVariable Long id) {
        Optional<Deposit> deposit = depositService.getDeposit(id);
        return deposit.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/deposits/{id}/close")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<Deposit> closeDeposit(@PathVariable Long id) {
        Deposit deposit = depositService.closeDeposit(id);
        return ResponseEntity.ok(deposit);
    }

    @GetMapping("/api/deposits/status/{status}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<List<Deposit>> getDepositsByStatus(@PathVariable DepositStatus status) {
        List<Deposit> deposits = depositService.getDepositsByStatus(status);
        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/ui/deposits")
    @PreAuthorize("hasRole('USER')")
    public String getDepositsPage(Model model, @AuthenticationPrincipal CustomerProfile customer) {
        List<Deposit> deposits = depositService.getDepositsByCustomer(customer);
        model.addAttribute("deposits", deposits);
        return "deposits";
    }

    @PostMapping("/ui/deposits")
    @PreAuthorize("hasRole('USER')")
    public String createDepositPage(@RequestParam Long accountId,
                                @RequestParam double principalAmount,
                                @RequestParam String currency,
                                @RequestParam double monthlyInterest,
                                @RequestParam int termMonths) {
        depositService.createDeposit(accountId, principalAmount, currency, monthlyInterest, termMonths);
        return "redirect:/ui/deposits";
    }
}
