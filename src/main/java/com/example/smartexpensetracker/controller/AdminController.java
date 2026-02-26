package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.repository.BusinessProfileRepository;
import com.example.smartexpensetracker.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessRepository;

    public AdminController(UserRepository userRepository,
                           BusinessProfileRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        // Add total counts
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalBusinesses", businessRepository.count());

        // Add full lists
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("businesses", businessRepository.findAll());

        return "admin-dashboard";
    }
}