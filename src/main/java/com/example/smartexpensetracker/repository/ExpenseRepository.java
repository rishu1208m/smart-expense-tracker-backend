package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Get all expenses by user email
    List<Expense> findByUserEmail(String email);

    // Get all expenses by user ID
    List<Expense> findByUserId(Long userId);

    // Get expenses between date range
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    // Monthly total using email
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.email = :email AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    Double getMonthlyTotal(@Param("email") String email,
                           @Param("year") int year,
                           @Param("month") int month);

    // Monthly total using userId
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    Double getTotalExpenseForMonth(@Param("userId") Long userId,
                                   @Param("year") int year,
                                   @Param("month") int month);

    // Total amount of all expenses
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Double getTotalAmountByUser(@Param("userId") Long userId);

    // Total count of expenses
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
    Long getTotalCountByUser(@Param("userId") Long userId);

    // Category-wise totals
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category")
    List<Object[]> getCategoryTotals(@Param("userId") Long userId);

    // Month-wise totals
    @Query("SELECT FUNCTION('MONTH', e.date), SUM(e.amount) FROM Expense e WHERE e.user.id = :userId GROUP BY FUNCTION('MONTH', e.date)")
    List<Object[]> getMonthlyTotals(@Param("userId") Long userId);
}