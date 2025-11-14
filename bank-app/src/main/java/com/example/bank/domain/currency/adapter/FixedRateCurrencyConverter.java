package com.example.bank.domain.currency.adapter;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class FixedRateCurrencyConverter implements CurrencyConverter {

    private static final Map<String, Double> BASE = Map.of(
            "KZT", 1.0000,
            "USD", 550.0000,
            "RUB", 6.0000
    );

    private static final Set<String> SUPPORTED = Set.of("USD", "KZT", "RUB");

    @Override
    public double convert(double amount, String from, String to) {
        if (from == null || to == null) throw new IllegalArgumentException("Currency codes must not be null");
        if (from.equalsIgnoreCase(to)) return amount;
        double r = rate(from, to);
        return Math.round(amount * r * 100.0) / 100.0;
    }

    @Override
    public double rate(String from, String to) {
        String f = from.toUpperCase();
        String t = to.toUpperCase();
        if (!SUPPORTED.contains(f) || !SUPPORTED.contains(t)) {
            throw new IllegalArgumentException("Unsupported currency pair: " + from + " -> " + to);
        }
        double kztPerFrom = BASE.get(f);
        double kztPerTo = BASE.get(t);
        return kztPerFrom / kztPerTo;
    }

    @Override
    public Set<String> supported() {
        return SUPPORTED;
    }

    @Override
    public Map<String, Double> baseRates() {
        return BASE;
    }
}
