package com.example.bank.domain.currency.adapter;

import java.util.Map;
import java.util.Set;

public interface CurrencyConverter {

    double convert(double amount, String from, String to);
    double rate(String from, String to);
    Set<String> supported();
    Map<String, Double> baseRates();
}
