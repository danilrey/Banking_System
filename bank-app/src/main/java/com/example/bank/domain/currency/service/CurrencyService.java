package com.example.bank.domain.currency.service;

import com.example.bank.domain.currency.adapter.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyConverter converter;

    public double convert(double amount, String from, String to) {
        return converter.convert(amount, from, to);
    }

    public double rate(String from, String to) {
        return converter.rate(from, to);
    }

    public Set<String> supported() {
        return converter.supported();
    }

    public Map<String, Double> baseRates() {
        return converter.baseRates();
    }
}
