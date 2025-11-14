package com.example.bank.api.rest;

import com.example.bank.domain.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/convert")
    public ResponseEntity<Double> convert(@RequestParam double amount,
                                          @RequestParam String from,
                                          @RequestParam String to) {
        return ResponseEntity.ok(currencyService.convert(amount, from, to));
    }

    @GetMapping("/rate")
    public ResponseEntity<Double> rate(@RequestParam String from,
                                       @RequestParam String to) {
        return ResponseEntity.ok(currencyService.rate(from, to));
    }

    @GetMapping("/supported")
    public ResponseEntity<Set<String>> supported() {
        return ResponseEntity.ok(currencyService.supported());
    }

    @GetMapping("/baseRates")
    public ResponseEntity<Map<String, Double>> baseRates() {
        return ResponseEntity.ok(currencyService.baseRates());
    }
}
