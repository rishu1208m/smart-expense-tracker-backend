package com.example.smartexpensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    // ✅ Optional injection — app won't crash if mail is not configured
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@expenseiq.com}")
    private String fromEmail;

    private boolean isMailAvailable() {
        return mailSender != null;
    }

    public void sendSimple(String to, String subject, String body) {
        if (!isMailAvailable()) {
            System.out.println("Mail not configured — skipping email to: " + to);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }
    }

    public void sendHtml(String to, String subject, String htmlBody) {
        if (!isMailAvailable()) {
            System.out.println("Mail not configured — skipping HTML email to: " + to);
            return;
        }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("HTML Email failed: " + e.getMessage());
        }
    }

    public void sendBudgetAlert(String to, String name, double spent, double budget) {
        if (!isMailAvailable()) return;
        double pct = (spent / budget) * 100;
        String subject = "⚠️ ExpenseIQ: You've used " + (int)pct + "% of your budget!";
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;max-width:560px;margin:0 auto;padding:32px;">
              <h1 style="color:#1a6bff;">⚠️ Budget Alert</h1>
              <p>Hi <strong>%s</strong>,</p>
              <p>You've spent <strong style="color:#ff6b6b;">₹%,.0f</strong> out of your 
              <strong>₹%,.0f</strong> monthly budget — that's <strong>%.0f%%</strong>!</p>
              <a href="https://smart-expense-tracker-frontend-dun.vercel.app/analytics" 
                 style="display:inline-block;background:#1a6bff;color:#fff;padding:12px 24px;
                 border-radius:10px;text-decoration:none;font-weight:700;">View Analytics →</a>
            </div>
            """.formatted(name, spent, budget, pct);
        sendHtml(to, subject, html);
    }

    public void sendMonthlySummary(String to, String name, double totalSpent, double budget, int expenseCount) {
        if (!isMailAvailable()) return;
        String subject = "📊 Your ExpenseIQ Monthly Summary";
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;max-width:560px;margin:0 auto;padding:32px;">
              <h1 style="color:#1a6bff;">📊 Monthly Summary</h1>
              <p>Hi <strong>%s</strong>, here's your month in review:</p>
              <p>💸 Total Spent: <strong>₹%,.0f</strong></p>
              <p>💰 Saved: <strong>₹%,.0f</strong></p>
              <p>📋 Expenses: <strong>%d</strong></p>
              <a href="https://smart-expense-tracker-frontend-dun.vercel.app/dashboard"
                 style="display:inline-block;background:#1a6bff;color:#fff;padding:12px 24px;
                 border-radius:10px;text-decoration:none;font-weight:700;">Open Dashboard →</a>
            </div>
            """.formatted(name, totalSpent, Math.max(0, budget - totalSpent), expenseCount);
        sendHtml(to, subject, html);
    }
}