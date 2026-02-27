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
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // ✅ Save expense for logged-in user
    public Expense saveExpenseForUser(Expense expense, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    // ✅ Get expenses for logged-in user
    public List<Expense> getExpensesByUser(String email) {
        return expenseRepository.findByUserEmail(email);
    }

    // ✅ Get expense by ID
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // ✅ Delete expense
    public void deleteExpenseForUser(Long id, String email) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this expense");
        }

        expenseRepository.delete(expense);
    }
    
    public Expense updateExpenseForUser(Long id, Expense updatedExpense, String email) {

        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // Check ownership
        if (!existingExpense.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to update this expense");
        }

        // Update fields
        existingExpense.setTitle(updatedExpense.getTitle());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setDate(updatedExpense.getDate());

        return expenseRepository.save(existingExpense);
    }
    
    public Double getMonthlyTotal(String email, int year, int month) {

        Double total = expenseRepository.getMonthlyTotal(email, year, month);

        return total != null ? total : 0.0;
    }
}