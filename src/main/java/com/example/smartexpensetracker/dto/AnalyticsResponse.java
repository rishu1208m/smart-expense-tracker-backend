package com.example.smartexpensetracker.dto;

import java.util.Map;

public class AnalyticsResponse {

    private Double totalAmount;
    private Long totalExpenses;
    private Map<String, Double> categoryTotals;
    private Map<String, Double> monthlyTotals;
    private String insight;

    public AnalyticsResponse(Double totalAmount,
                             Long totalExpenses,
                             Map<String, Double> categoryTotals,
                             Map<String, Double> monthlyTotals,
                             String insight) {
        this.totalAmount = totalAmount;
        this.totalExpenses = totalExpenses;
        this.categoryTotals = categoryTotals;
        this.monthlyTotals = monthlyTotals;
        this.insight = insight;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Long getTotalExpenses() {
        return totalExpenses;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }

    public Map<String, Double> getMonthlyTotals() {
        return monthlyTotals;
    }

    public String getInsight() {
        return insight;
    }
}