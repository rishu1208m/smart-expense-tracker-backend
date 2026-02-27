package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndYearAndMonth(Long userId, int year, int month);
}