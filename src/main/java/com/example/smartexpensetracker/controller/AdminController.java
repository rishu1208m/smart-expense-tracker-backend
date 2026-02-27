package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.repository.ExpenseRepository;
import com.example.smartexpensetracker.repository.BudgetRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public AdminController(UserRepository userRepository,
                           ExpenseRepository expenseRepository,
                           BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalExpenses", expenseRepository.count());
        model.addAttribute("totalBudgets", budgetRepository.count());

        return "admin-dashboard";
    }
}