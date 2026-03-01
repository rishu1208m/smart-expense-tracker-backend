package com.example.smartexpensetracker.service;

import com.example.smartexpensetracker.entity.Expense;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.ExpenseRepository;
import com.example.smartexpensetracker.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository    userRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository    = userRepository;
    }

    // Save expense for logged-in user
    public Expense saveExpenseForUser(Expense expense, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    // Get all expenses for logged-in user
    public List<Expense> getExpensesByUser(String email) {
        return expenseRepository.findByUserEmail(email);
    }

    // Get expense by ID
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // Delete expense (with ownership check)
    public void deleteExpenseForUser(Long id, String email) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this expense");
        }
        expenseRepository.delete(expense);
    }

    // Update expense (with ownership check)
    public Expense updateExpenseForUser(Long id, Expense updatedExpense, String email) {
        Expense existing = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!existing.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to update this expense");
        }
        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDate(updatedExpense.getDate());
        existing.setNote(updatedExpense.getNote());
        return expenseRepository.save(existing);
    }

    // Get monthly total spending
    public Double getMonthlyTotal(String email, int year, int month) {
        Double total = expenseRepository.getMonthlyTotal(email, year, month);
        return total != null ? total : 0.0;
    }
}