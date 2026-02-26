package com.example.smartexpensetracker.service;

import org.springframework.stereotype.Service;

@Service
public class AIContentService {

    public String generateAboutUs(String name, String category, String location) {
        return "Welcome to " + name +
                ", a trusted " + category +
                " company located in " + location +
                ". We focus on quality, innovation and customer satisfaction.";
    }

    public String generateServices(String category, String audience) {
        return "We provide professional " + category +
                " services specially designed for " + audience +
                ". Our solutions are reliable and performance driven.";
    }

    public String generateMetaTitle(String name, String category) {
        return name + " | Professional " + category + " Services";
    }

    public String generateMetaDescription(String name, String category, String location) {
        return name + " offers expert " + category +
                " solutions in " + location +
                ". Contact us today for quality service.";
    }

    public String generateKeywords(String category, String location) {
        return category + ", " + location + ", professional services";
    }
}