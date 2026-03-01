package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.entity.Expense;
import com.example.smartexpensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // ── Used by ExpenseService ──
    List<Expense> findByUserEmail(String email);

    // ── Used by AnalyticsController ──
    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    // ── Used by DashboardController ──
    List<Expense> findByUser(User user);

    List<Expense> findByUserAndCategory(User user, String category);

    // ── Used by ExpenseService.getMonthlyTotal() ──
    @Query("SELECT SUM(e.amount) FROM Expense e " +
           "WHERE e.user.email = :email " +
           "AND YEAR(e.date) = :year " +
           "AND MONTH(e.date) = :month")
    Double getMonthlyTotal(@Param("email") String email,
                           @Param("year")  int year,
                           @Param("month") int month);

    // ── Used by BudgetController ──
    @Query("SELECT SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND YEAR(e.date) = :year " +
           "AND MONTH(e.date) = :month")
    Double getTotalExpenseForMonth(@Param("userId") Long userId,
                                   @Param("year")   int year,
                                   @Param("month")  int month);
}