package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.dto.AnalyticsResponse;
import com.example.smartexpensetracker.entity.Expense;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.ExpenseRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.service.AIInsightService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AIInsightService aiInsightService;

    public AnalyticsController(ExpenseRepository expenseRepository,
                               UserRepository userRepository,
                               AIInsightService aiInsightService) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.aiInsightService = aiInsightService;
    }

    @GetMapping
    public AnalyticsResponse getAnalytics(
            Authentication authentication,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        List<Expense> expenses;

        if (start != null && end != null) {
            expenses = expenseRepository.findByUserIdAndDateBetween(userId, start, end);
        } else {
            expenses = expenseRepository.findByUserId(userId);
        }

        double totalAmount = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        long totalCount = expenses.size();

        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Double> monthMap = new HashMap<>();

        for (Expense e : expenses) {
            categoryMap.merge(e.getCategory(), e.getAmount(), Double::sum);

            String month = e.getDate().getMonth().toString();
            monthMap.merge(month, e.getAmount(), Double::sum);
        }

        // ✅ Generate AI Insight
        String insight = aiInsightService.generateInsight(totalAmount, categoryMap);

        return new AnalyticsResponse(
                totalAmount,
                totalCount,
                categoryMap,
                monthMap,
                insight
        );
    }
}