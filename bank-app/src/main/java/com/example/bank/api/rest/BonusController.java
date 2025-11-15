package com.example.bank.api.rest;

import com.example.bank.domain.bonus.model.BonusAccount;
import com.example.bank.domain.bonus.service.BonusAccountService;
import com.example.bank.domain.customer.model.CustomerProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bonuses")
@RequiredArgsConstructor
public class BonusController {

    private final BonusAccountService bonusAccountService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BonusAccountResponse> createBonusAccount(@RequestBody CreateBonusAccountRequest request) {
        BonusAccount account = bonusAccountService.createBonusAccount(request.getCustomerId());
        return ResponseEntity.status(201).body(mapToResponse(account));
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BonusAccountResponse> getBonusAccount(@PathVariable Long customerId) {
        BonusAccount account = bonusAccountService.getBonusAccount(customerId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(account));
    }

    @PostMapping("/add/{category}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> addBonus(@AuthenticationPrincipal CustomerProfile customer, @PathVariable String category) {
        Long customerId = customer.getId();
        switch (category.toUpperCase()) {
            case "GROCERY" -> bonusAccountService.addGroceryBonus();
            case "FUEL" -> bonusAccountService.addFuelBonus();
            case "ENTERTAINMENT" -> bonusAccountService.addEntertainmentBonus();
            case "TRAVEL" -> bonusAccountService.addTravelBonus();
            case "HEALTH" -> bonusAccountService.addHealthBonus();
            case "EDUCATION" -> bonusAccountService.addEducationBonus();
            case "OTHER" -> bonusAccountService.addOtherBonus();
            default -> {
                return ResponseEntity.badRequest().body("Invalid category");
            }
        }
        return ResponseEntity.ok("Bonus added for category: " + category);
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApplyCashbackResponse> applyCashback(@AuthenticationPrincipal CustomerProfile customer, @RequestBody ApplyCashbackRequest request) {
        Long customerId = customer.getId();
        BigDecimal cashback = bonusAccountService.calculateAndApplyCashback(
                customerId,
                request.getAmount(),
                request.getCategory()
        );
        return ResponseEntity.ok(new ApplyCashbackResponse(cashback));
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BonusAccountResponse> getMyBonusAccount(@AuthenticationPrincipal CustomerProfile customer) {
        BonusAccount account = bonusAccountService.getBonusAccount(customer.getId());
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(account));
    }

    @Data
    public static class CreateBonusAccountRequest {
        private Long customerId;
    }

    @Data
    public static class BonusAccountResponse {
        private Long id;
        private Long customerId;
        private BigDecimal balance;
    }

    @Data
    public static class ApplyCashbackRequest {
        private BigDecimal amount;
        private String category;
    }

    @Data
    public static class ApplyCashbackResponse {
        private BigDecimal cashbackAmount;

        public ApplyCashbackResponse(BigDecimal cashbackAmount) {
            this.cashbackAmount = cashbackAmount;
        }
    }

    private BonusAccountResponse mapToResponse(BonusAccount account) {
        BonusAccountResponse response = new BonusAccountResponse();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setBalance(account.getBalance());
        return response;
    }
}
