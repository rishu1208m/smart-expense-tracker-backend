package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.entity.Expense;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.ExpenseRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.service.AIInsightService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository    userRepository;
    private final AIInsightService  aiInsightService;

    public AnalyticsController(ExpenseRepository expenseRepository,
                               UserRepository userRepository,
                               AIInsightService aiInsightService) {
        this.expenseRepository = expenseRepository;
        this.userRepository    = userRepository;
        this.aiInsightService  = aiInsightService;
    }

    // ─────────────────────────────────────────
    //  GET /api/analytics
    //  Optional params: startDate, endDate (yyyy-MM-dd)
    // ─────────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> getAnalytics(
            Authentication authentication,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // Get expenses (filtered by date range or all)
        List<Expense> expenses;
        if (startDate != null && endDate != null) {
            expenses = expenseRepository.findByUserIdAndDateBetween(
                userId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
            );
        } else {
            expenses = expenseRepository.findByUserId(userId);
        }

        // Total amount & count
        double totalAmount = expenses.stream()
            .mapToDouble(Expense::getAmount)
            .sum();
        long totalCount = expenses.size();

        // Category breakdown  e.g. { "Food": 1200, "Travel": 800 }
        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Double> monthMap    = new HashMap<>();

        for (Expense e : expenses) {
            // category totals
            String cat = e.getCategory() != null ? e.getCategory() : "Other";
            categoryMap.merge(cat, e.getAmount(), Double::sum);

            // monthly totals
            if (e.getDate() != null) {
                String month = e.getDate().getMonth().toString();
                monthMap.merge(month, e.getAmount(), Double::sum);
            }
        }

        // AI insight
        String insight = aiInsightService.generateInsight(totalAmount, categoryMap);

        // Build response map (avoids needing AnalyticsResponse DTO)
        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount",   totalAmount);
        response.put("totalCount",    totalCount);
        response.put("categoryBreakdown", categoryMap);
        response.put("monthlyBreakdown",  monthMap);
        response.put("insight",       insight);

        return ResponseEntity.ok(response);
    }
}