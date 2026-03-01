package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.service.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private ExpenseService expenseService;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getDashboard(Authentication authentication) {

        try {
            // Get email directly from Spring Security — no JwtUtil needed!
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate now        = LocalDate.now();
            int currentMonth     = now.getMonthValue();
            int currentYear      = now.getYear();

            // Total spent this month
            double totalSpent    = expenseService.getMonthlyTotal(email, currentYear, currentMonth);
            double monthlyBudget = user.getMonthlyBudget() != null ? user.getMonthlyBudget() : 0;
            double budgetLeft    = monthlyBudget - totalSpent;
            double savings       = Math.max(budgetLeft, 0);

            // Expense count this month
            long expenseCount = expenseService.getExpensesByUser(email).stream()
                .filter(e -> e.getDate() != null
                    && e.getDate().getMonthValue() == currentMonth
                    && e.getDate().getYear() == currentYear)
                .count();

            // Last 8 months trend for chart
            List<Map<String, Object>> monthlySpend = new ArrayList<>();
            for (int i = 7; i >= 0; i--) {
                LocalDate d     = now.minusMonths(i);
                double total    = expenseService.getMonthlyTotal(email, d.getYear(), d.getMonthValue());
                Map<String, Object> pt = new HashMap<>();
                pt.put("month", Month.of(d.getMonthValue())
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                pt.put("spent", total);
                monthlySpend.add(pt);
            }

            // Currency symbol — e.g. "₹" from "₹ INR"
            String currency = user.getCurrency() != null
                ? user.getCurrency().split(" ")[0] : "₹";

            // Build response
            Map<String, Object> res = new HashMap<>();
            res.put("totalSpent",    currency + String.format("%.0f", totalSpent));
            res.put("budgetLeft",    currency + String.format("%.0f", Math.max(budgetLeft, 0)));
            res.put("savings",       currency + String.format("%.0f", savings));
            res.put("expenseCount",  expenseCount);
            res.put("spentChange",   "this month");
            res.put("budgetChange",  "remaining");
            res.put("savingsChange", "saved");
            res.put("monthlySpend",  monthlySpend);
            res.put("currency",      currency);
            res.put("monthlyBudget", monthlyBudget);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}