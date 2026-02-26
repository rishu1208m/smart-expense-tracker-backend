package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.model.BusinessProfile;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.BusinessProfileRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final BusinessProfileRepository businessRepository;
    private final UserRepository userRepository;

    public DashboardController(BusinessProfileRepository businessRepository,
                               UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        List<BusinessProfile> businesses = businessRepository.findByUserId(user.getId());

        model.addAttribute("businesses", businesses);

        return "dashboard";
    }
}