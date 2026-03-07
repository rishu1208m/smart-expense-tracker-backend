package com.example.smartexpensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double targetAmount = 0.0;
    private Double savedAmount  = 0.0;
    private String icon  = "🎯";
    private String color = "#1a6bff";
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    // Getters & Setters
    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public void   setName(String n)      { this.name = n; }
    public Double getTargetAmount()      { return targetAmount; }
    public void   setTargetAmount(Double a){ this.targetAmount = a; }
    public Double getSavedAmount()       { return savedAmount == null ? 0.0 : savedAmount; }
    public void   setSavedAmount(Double a){ this.savedAmount = a; }
    public String getIcon()              { return icon; }
    public void   setIcon(String i)      { this.icon = i; }
    public String getColor()             { return color; }
    public void   setColor(String c)     { this.color = c; }
    public LocalDate getDeadline()       { return deadline; }
    public void   setDeadline(LocalDate d){ this.deadline = d; }
    public User   getUser()              { return user; }
    public void   setUser(User u)        { this.user = u; }
}