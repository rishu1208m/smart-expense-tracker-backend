package com.example.smartexpensetracker.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIInsightService {

    public String generateInsight(Double totalAmount,
                                  Map<String, Double> categories) {

        if (categories == null || categories.isEmpty()) {
            return "No spending data available.";
        }

        String highestCategory = categories.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        return "You are spending most on " + highestCategory +
                ". Total spending is ₹" + totalAmount +
                ". Consider optimizing this category.";
    }
}