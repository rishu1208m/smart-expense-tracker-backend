package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.entity.Budget;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.repository.BudgetRepository;
import com.example.smartexpensetracker.repository.ExpenseRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardController(UserRepository userRepository,
                               BudgetRepository budgetRepository,
                               ExpenseRepository expenseRepository) {

        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        LocalDate now = LocalDate.now();

        Budget budget = budgetRepository
                .findByUserIdAndYearAndMonth(user.getId(), now.getYear(), now.getMonthValue())
                .orElse(null);

        Double totalExpense = expenseRepository
                .getTotalExpenseForMonth(user.getId(), now.getYear(), now.getMonthValue());

        if (totalExpense == null) totalExpense = 0.0;

        model.addAttribute("dashboardBudget", budget);
        model.addAttribute("dashboardTotalExpense", totalExpense);

        if (budget != null) {
            double remaining = budget.getAmount() - totalExpense;
            double percentUsed = (totalExpense / budget.getAmount()) * 100;

            model.addAttribute("dashboardRemaining", remaining);
            model.addAttribute("dashboardPercentUsed", percentUsed);
        }

        return "dashboard";
    }
}