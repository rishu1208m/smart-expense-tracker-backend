package com.example.smartexpensetracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**")
        .allowedOrigins(
            "http://localhost:5173",
            "https://smart-expense-tracker-frontend-dun.vercel.app",
            "https://smart-expense-tracker-frontend-git-main-rishu1208ms-projects.vercel.app",
            "https://smart-expense-tracker-frontend-ham9rcveu-rishu1208ms-projects.vercel.app"
        )
        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
        .allowedHeaders("*")
        .allowCredentials(true);
    }
}	