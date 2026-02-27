package com.example.smartexpensetracker.service;

import org.springframework.stereotype.Service;

@Service
public class AIContentService {

    public String generateAboutUs(String name, String category, String location) {
        return "Welcome to " + name + ", a trusted " + category +
                " business located in " + location +
                ". We are committed to delivering excellence and quality service to our customers.";
    }

    public String generateServices(String category, String targetAudience) {
        return "We provide professional " + category +
                " services tailored especially for " + targetAudience +
                ". Our solutions are reliable, affordable, and result-driven.";
    }

    public String generateMetaTitle(String name, String category) {
        return name + " | Best " + category + " Services";
    }

    public String generateMetaDescription(String name, String category, String location) {
        return name + " offers top-quality " + category +
                " services in " + location +
                ". Contact us today for professional solutions.";
    }

    public String generateKeywords(String category, String location) {
        return category + ", " + location + ", professional services, trusted business";
    }
}