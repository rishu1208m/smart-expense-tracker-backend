package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndYearAndMonth(Long userId, int year, int month);
}