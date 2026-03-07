package com.example.smartexpensetracker.repository;

import com.example.smartexpensetracker.model.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserEmail(String email);
}