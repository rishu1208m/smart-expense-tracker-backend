package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.model.LoginRequest;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // ─────────────────────────────────────────
    //  REGISTER  →  POST /api/auth/register
    // ─────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        // 1. Check duplicate email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "An account with this email already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // 2. Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Default role
        user.setRole("CLIENT");

        // 4. Save to DB
        User savedUser = userRepository.save(user);

        // 5. Generate JWT
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // 6. Return JSON that React frontend reads
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", savedUser.getEmail());
        response.put("name",  savedUser.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─────────────────────────────────────────
    //  LOGIN  →  POST /api/auth/login
    // ─────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1. Authenticate credentials
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // 2. Load user from DB
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        User user = userOpt.get();

        // 3. Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());

        // 4. Return JSON that React frontend reads
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("name",  user.getFullName());
        return ResponseEntity.ok(response);
    }
}