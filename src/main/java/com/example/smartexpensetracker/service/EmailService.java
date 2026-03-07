package com.example.smartexpensetracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Send plain text email
    public void sendSimple(String to, String subject, String body) {
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

    // Send HTML email
    public void sendHtml(String to, String subject, String htmlBody) {
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

    // Budget warning email
    public void sendBudgetAlert(String to, String name, double spent, double budget) {
        double pct = (spent / budget) * 100;
        String subject = "⚠️ ExpenseIQ: You've used " + (int)pct + "% of your budget!";
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;max-width:560px;margin:0 auto;background:#f5f7ff;padding:32px;border-radius:16px;">
              <div style="background:linear-gradient(135deg,#0f1c3f,#1a6bff);border-radius:12px;padding:28px;text-align:center;margin-bottom:24px;">
                <div style="font-size:40px;margin-bottom:8px;">⚠️</div>
                <h1 style="color:#fff;font-size:22px;margin:0;">Budget Alert</h1>
              </div>
              <p style="color:#0f1c3f;font-size:16px;">Hi <strong>%s</strong>,</p>
              <p style="color:#4a5578;font-size:15px;">You've spent <strong style="color:#ff6b6b;">₹%,.0f</strong> out of your <strong>₹%,.0f</strong> monthly budget — that's <strong style="color:#ff6b6b;">%.0f%%</strong>!</p>
              <div style="background:#fff;border-radius:12px;padding:20px;margin:20px 0;border:1px solid #e4e9f7;">
                <div style="background:#f0f4ff;border-radius:6px;height:12px;overflow:hidden;">
                  <div style="width:%.0f%%;height:100%%;background:linear-gradient(90deg,#1a6bff,#ff6b6b);border-radius:6px;"></div>
                </div>
                <div style="display:flex;justify-content:space-between;margin-top:8px;font-size:13px;color:#8892b0;">
                  <span>₹0</span><span>₹%,.0f</span>
                </div>
              </div>
              <p style="color:#4a5578;font-size:14px;">Review your expenses and stay on track! 💪</p>
              <a href="https://smart-expense-tracker-frontend-dun.vercel.app/analytics" style="display:inline-block;background:linear-gradient(135deg,#1a6bff,#0d4fd9);color:#fff;text-decoration:none;padding:12px 24px;border-radius:10px;font-weight:700;font-size:14px;margin-top:8px;">View Analytics →</a>
            </div>
            """.formatted(name, spent, budget, pct, Math.min(pct, 100), budget);
        sendHtml(to, subject, html);
    }

    // Monthly summary email
    public void sendMonthlySummary(String to, String name, double totalSpent, double budget, int expenseCount) {
        String subject = "📊 Your ExpenseIQ Monthly Summary";
        String html = """
            <div style="font-family:'Segoe UI',sans-serif;max-width:560px;margin:0 auto;background:#f5f7ff;padding:32px;border-radius:16px;">
              <div style="background:linear-gradient(135deg,#0f1c3f,#1a6bff);border-radius:12px;padding:28px;text-align:center;margin-bottom:24px;">
                <div style="font-size:40px;margin-bottom:8px;">📊</div>
                <h1 style="color:#fff;font-size:22px;margin:0;">Monthly Summary</h1>
              </div>
              <p style="color:#0f1c3f;font-size:16px;">Hi <strong>%s</strong>, here's your month in review:</p>
              <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:12px;margin:20px 0;">
                <div style="background:#fff;border-radius:12px;padding:16px;text-align:center;border:1px solid #e4e9f7;">
                  <div style="font-size:22px;font-weight:800;color:#0f1c3f;">₹%,.0f</div>
                  <div style="font-size:11px;color:#8892b0;margin-top:4px;">TOTAL SPENT</div>
                </div>
                <div style="background:#fff;border-radius:12px;padding:16px;text-align:center;border:1px solid #e4e9f7;">
                  <div style="font-size:22px;font-weight:800;color:#10b981;">₹%,.0f</div>
                  <div style="font-size:11px;color:#8892b0;margin-top:4px;">SAVED</div>
                </div>
                <div style="background:#fff;border-radius:12px;padding:16px;text-align:center;border:1px solid #e4e9f7;">
                  <div style="font-size:22px;font-weight:800;color:#1a6bff;">%d</div>
                  <div style="font-size:11px;color:#8892b0;margin-top:4px;">EXPENSES</div>
                </div>
              </div>
              <a href="https://smart-expense-tracker-frontend-dun.vercel.app/dashboard" style="display:inline-block;background:linear-gradient(135deg,#1a6bff,#0d4fd9);color:#fff;text-decoration:none;padding:12px 24px;border-radius:10px;font-weight:700;font-size:14px;margin-top:8px;">Open Dashboard →</a>
            </div>
            """.formatted(name, totalSpent, Math.max(0, budget - totalSpent), expenseCount);
        sendHtml(to, subject, html);
    }
}