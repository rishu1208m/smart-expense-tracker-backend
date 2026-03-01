package com.example.smartexpensetracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private int year;
    private int month;
    private Long userId;

    public Budget() {}

    public Budget(Double amount, int year, int month, Long userId) {
        this.amount = amount;
        this.year   = year;
        this.month  = month;
        this.userId = userId;
    }

    // ═══ GETTERS ═══
    public Long   getId()     { return id; }
    public Double getAmount() { return amount; }
    public int    getYear()   { return year; }
    public int    getMonth()  { return month; }
    public Long   getUserId() { return userId; }

    // ═══ SETTERS ═══  ← these were missing!
    public void setAmount(Double amount) { this.amount = amount; }
    public void setYear(int year)        { this.year = year; }
    public void setMonth(int month)      { this.month = month; }
    public void setUserId(Long userId)   { this.userId = userId; }
}