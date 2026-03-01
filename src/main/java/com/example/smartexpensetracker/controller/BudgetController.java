package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.entity.Budget;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.BudgetRepository;
import com.example.smartexpensetracker.repository.ExpenseRepository;
import com.example.smartexpensetracker.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetRepository  budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository    userRepository;

    public BudgetController(BudgetRepository budgetRepository,
                            ExpenseRepository expenseRepository,
                            UserRepository userRepository) {
        this.budgetRepository  = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository    = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getBudget(Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate now = LocalDate.now();

        Optional<Budget> budgetOpt = budgetRepository
            .findByUserIdAndYearAndMonth(user.getId(), now.getYear(), now.getMonthValue());

        Double totalExpense = expenseRepository
            .getTotalExpenseForMonth(user.getId(), now.getYear(), now.getMonthValue());
        if (totalExpense == null) totalExpense = 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("totalExpense", totalExpense);
        response.put("year",         now.getYear());
        response.put("month",        now.getMonthValue());

        if (budgetOpt.isPresent()) {
            Budget budget      = budgetOpt.get();
            double remaining   = budget.getAmount() - totalExpense;
            double percentUsed = budget.getAmount() > 0
                ? (totalExpense / budget.getAmount()) * 100 : 0;
            response.put("budgetAmount", budget.getAmount());
            response.put("remaining",    remaining);
            response.put("percentUsed",  percentUsed);
        } else {
            response.put("budgetAmount", 0);
            response.put("remaining",    0);
            response.put("percentUsed",  0);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> saveBudget(@RequestBody Map<String, Double> body,
                                        Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate now  = LocalDate.now();
        Double amount  = body.get("amount");

        Optional<Budget> existing = budgetRepository
            .findByUserIdAndYearAndMonth(user.getId(), now.getYear(), now.getMonthValue());

        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setAmount(amount);
        } else {
            budget = new Budget(amount, now.getYear(), now.getMonthValue(), user.getId());
        }

        budgetRepository.save(budget);

        return ResponseEntity.ok(Map.of(
            "message", "Budget saved successfully",
            "amount",  amount
        ));
    }
}