package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.model.SavingsGoal;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.SavingsGoalRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = {"http://localhost:5173","https://smart-expense-tracker-frontend-dun.vercel.app","https://smart-expense-tracker-frontend-git-main-rishu1208ms-projects.vercel.app","https://smart-expense-tracker-frontend-ham9rcveu-rishu1208ms-projects.vercel.app"})
public class Goalscontroller {

    private final SavingsGoalRepository goalsRepo;
    private final UserRepository        userRepo;

    public Goalscontroller(SavingsGoalRepository goalsRepo, UserRepository userRepo) {
        this.goalsRepo = goalsRepo;
        this.userRepo  = userRepo;
    }

    // GET all goals for current user
    @GetMapping
    public ResponseEntity<List<SavingsGoal>> getGoals(Authentication auth) {
        return ResponseEntity.ok(goalsRepo.findByUserEmail(auth.getName()));
    }

    // POST create new goal
    @PostMapping
    public ResponseEntity<SavingsGoal> createGoal(@RequestBody SavingsGoal goal, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        goal.setUser(user);
        goal.setSavedAmount(0.0);
        return ResponseEntity.ok(goalsRepo.save(goal));
    }

    // POST add money to a goal
    @PostMapping("/{id}/add")
    public ResponseEntity<?> addMoney(@PathVariable Long id,
                                       @RequestBody Map<String, Double> body,
                                       Authentication auth) {
        SavingsGoal goal = goalsRepo.findById(id).orElse(null);
        if (goal == null) return ResponseEntity.notFound().build();
        if (!goal.getUser().getEmail().equals(auth.getName()))
            return ResponseEntity.status(403).build();

        Double toAdd = body.getOrDefault("amount", 0.0);
        goal.setSavedAmount(goal.getSavedAmount() + toAdd);
        return ResponseEntity.ok(goalsRepo.save(goal));
    }

    // DELETE a goal
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id, Authentication auth) {
        SavingsGoal goal = goalsRepo.findById(id).orElse(null);
        if (goal == null) return ResponseEntity.notFound().build();
        if (!goal.getUser().getEmail().equals(auth.getName()))
            return ResponseEntity.status(403).build();
        goalsRepo.delete(goal);
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}