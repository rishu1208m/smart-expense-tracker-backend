package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.entity.Expense;
import com.example.smartexpensetracker.service.ExpenseService;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ✅ Create expense (linked to logged-in user)
    @PostMapping
    public Expense createExpense(@RequestBody Expense expense,
                                 Principal principal) {

        String email = principal.getName();

        return expenseService.saveExpenseForUser(expense, email);
    }

    // ✅ Get expenses of logged-in user only
    @GetMapping
    public List<Expense> getMyExpenses(Principal principal) {

        String email = principal.getName();

        return expenseService.getExpensesByUser(email);
    }

    // ✅ Get expense by ID
    @GetMapping("/{id}")
    public Optional<Expense> getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    // ✅ Delete expense
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id, Principal principal) {

        String email = principal.getName();

        expenseService.deleteExpenseForUser(id, email);
    }
    
    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id,
                                 @RequestBody Expense expense,
                                 Principal principal) {

        String email = principal.getName();

        return expenseService.updateExpenseForUser(id, expense, email);
    }
    
    @GetMapping("/summary/monthly")
    public Double getMonthlySummary(@RequestParam int year,
                                    @RequestParam int month,
                                    Principal principal) {

        String email = principal.getName();

        return expenseService.getMonthlyTotal(email, year, month);
    }
}