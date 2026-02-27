package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.entity.Budget;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.BudgetRepository;
import com.example.smartexpensetracker.repository.ExpenseRepository;
import com.example.smartexpensetracker.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public BudgetController(BudgetRepository budgetRepository,
                            ExpenseRepository expenseRepository,
                            UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showBudget(Model model, Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        LocalDate now = LocalDate.now();

        Budget budget = budgetRepository
                .findByUserIdAndYearAndMonth(user.getId(), now.getYear(), now.getMonthValue())
                .orElse(null);

        Double totalExpense = expenseRepository
                .getTotalExpenseForMonth(user.getId(), now.getYear(), now.getMonthValue());

        if (totalExpense == null) totalExpense = 0.0;

        model.addAttribute("budget", budget);
        model.addAttribute("totalExpense", totalExpense);

        if (budget != null) {
            double remaining = budget.getAmount() - totalExpense;
            model.addAttribute("remaining", remaining);

            double percentUsed = (totalExpense / budget.getAmount()) * 100;
            model.addAttribute("percentUsed", percentUsed);
        }

        return "budget";
    }

    @PostMapping("/save")
    public String saveBudget(@RequestParam Double amount,
                             Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        LocalDate now = LocalDate.now();

        Budget budget = new Budget(amount, now.getYear(), now.getMonthValue(), user.getId());

        budgetRepository.save(budget);

        return "redirect:/budget";
    }
}