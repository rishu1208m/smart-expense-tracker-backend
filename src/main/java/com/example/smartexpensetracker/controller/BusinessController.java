package com.example.smartexpensetracker.controller;

import com.example.smartexpensetracker.model.BusinessProfile;
import com.example.smartexpensetracker.model.User;
import com.example.smartexpensetracker.repository.BusinessProfileRepository;
import com.example.smartexpensetracker.repository.UserRepository;
import com.example.smartexpensetracker.service.AIContentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BusinessController {

    private final BusinessProfileRepository businessRepository;
    private final UserRepository userRepository;
    private final AIContentService aiService;

    public BusinessController(BusinessProfileRepository businessRepository,
                              UserRepository userRepository,
                              AIContentService aiService) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

    // ================= CREATE BUSINESS =================

    @GetMapping("/business/create")
    public String showForm(Model model) {
        model.addAttribute("business", new BusinessProfile());
        return "business-form";
    }

    // ================= SAVE / UPDATE BUSINESS =================

    @PostMapping("/business/save")
    public String saveBusiness(@ModelAttribute BusinessProfile business,
                               Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {

            // Generate AI content only for NEW business
            if (business.getId() == null) {

                business.setAboutUs(
                        aiService.generateAboutUs(
                                business.getBusinessName(),
                                business.getCategory(),
                                business.getLocation()
                        )
                );

                business.setServices(
                        aiService.generateServices(
                                business.getCategory(),
                                business.getTargetAudience()
                        )
                );

                business.setMetaTitle(
                        aiService.generateMetaTitle(
                                business.getBusinessName(),
                                business.getCategory()
                        )
                );

                business.setMetaDescription(
                        aiService.generateMetaDescription(
                                business.getBusinessName(),
                                business.getCategory(),
                                business.getLocation()
                        )
                );

                business.setKeywords(
                        aiService.generateKeywords(
                                business.getCategory(),
                                business.getLocation()
                        )
                );
            }

            business.setUser(user);
            businessRepository.save(business);
        }

        return "redirect:/dashboard";
    }

    // ================= EDIT BUSINESS =================

    @GetMapping("/business/edit/{id}")
    public String editBusiness(@PathVariable Long id,
                               Model model,
                               Authentication authentication) {

        User user = getLoggedInUser(authentication);
        BusinessProfile business = businessRepository.findById(id).orElse(null);

        if (business != null && user != null &&
                business.getUser().getId().equals(user.getId())) {

            model.addAttribute("business", business);
            return "business-form";
        }

        return "redirect:/dashboard";
    }

    // ================= PREVIEW WEBSITE =================

    @GetMapping("/business/preview/{id}")
    public String previewWebsite(@PathVariable Long id,
                                 Model model,
                                 Authentication authentication) {

        User user = getLoggedInUser(authentication);
        BusinessProfile business = businessRepository.findById(id).orElse(null);

        if (business != null && user != null &&
                business.getUser().getId().equals(user.getId())) {

            model.addAttribute("business", business);
            return "website-preview";
        }

        return "redirect:/dashboard";
    }

    // ================= DOWNLOAD WEBSITE =================

    @GetMapping("/business/download/{id}")
    @ResponseBody
    public ResponseEntity<String> downloadWebsite(@PathVariable Long id,
                                                  Authentication authentication) {

        User user = getLoggedInUser(authentication);
        BusinessProfile business = businessRepository.findById(id).orElse(null);

        if (business != null && user != null &&
                business.getUser().getId().equals(user.getId())) {

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>%s</title>
                        <meta name="description" content="%s">
                        <meta name="keywords" content="%s">
                    </head>
                    <body>
                        <h1>%s</h1>

                        <h2>About Us</h2>
                        <p>%s</p>

                        <h2>Services</h2>
                        <p>%s</p>
                    </body>
                    </html>
                    """.formatted(
                    business.getMetaTitle(),
                    business.getMetaDescription(),
                    business.getKeywords(),
                    business.getBusinessName(),
                    business.getAboutUs(),
                    business.getServices()
            );

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=website.html")
                    .body(html);
        }

        return ResponseEntity.badRequest().build();
    }

    // ================= DELETE BUSINESS =================

    @GetMapping("/business/delete/{id}")
    public String deleteBusiness(@PathVariable Long id,
                                 Authentication authentication) {

        User user = getLoggedInUser(authentication);
        BusinessProfile business = businessRepository.findById(id).orElse(null);

        if (business != null && user != null &&
                business.getUser().getId().equals(user.getId())) {

            businessRepository.delete(business);
        }

        return "redirect:/dashboard";
    }

    // ================= HELPER METHOD =================

    private User getLoggedInUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}