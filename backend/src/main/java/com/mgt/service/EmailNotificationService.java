package com.mgt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@rural.com}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    // ── Order placed ──────────────────────────────────────────────────────────
    @Async
    public void sendOrderConfirmation(String toEmail, String customerName,
                                      String orderCode, double totalAmount) {
        String subject = "Order Confirmed — " + orderCode;
        String body = "Dear " + customerName + ",\n\n"
                + "Thank you for your order at Rural!\n\n"
                + "Order Code : " + orderCode + "\n"
                + "Total       : ৳" + String.format("%.2f", totalAmount) + "\n\n"
                + "We will notify you once your order is shipped.\n\n"
                + "— Rural Team";
        send(toEmail, subject, body);
    }

    // ── Order shipped ─────────────────────────────────────────────────────────
    @Async
    public void sendOrderShipped(String toEmail, String customerName,
                                 String orderCode, String trackingNumber, String courier) {
        String subject = "Your Order Has Been Shipped — " + orderCode;
        String body = "Dear " + customerName + ",\n\n"
                + "Great news! Your order " + orderCode + " has been shipped.\n\n"
                + "Courier         : " + courier + "\n"
                + "Tracking Number : " + trackingNumber + "\n\n"
                + "You can track your package using the tracking number above.\n\n"
                + "— Rural Team";
        send(toEmail, subject, body);
    }

    // ── Order delivered ───────────────────────────────────────────────────────
    @Async
    public void sendOrderDelivered(String toEmail, String customerName, String orderCode) {
        String subject = "Order Delivered — " + orderCode;
        String body = "Dear " + customerName + ",\n\n"
                + "Your order " + orderCode + " has been delivered!\n\n"
                + "We hope you love your purchase. If you have a moment, "
                + "please leave a review — it helps our artisans greatly.\n\n"
                + "— Rural Team";
        send(toEmail, subject, body);
    }

    // ── Seller: new order notification ────────────────────────────────────────
    @Async
    public void sendSellerNewOrder(String sellerEmail, String sellerName,
                                   String orderCode, String productName, int qty) {
        String subject = "New Order Received — " + orderCode;
        String body = "Dear " + sellerName + ",\n\n"
                + "You have received a new order on Rural!\n\n"
                + "Order Code : " + orderCode + "\n"
                + "Product    : " + productName + "\n"
                + "Quantity   : " + qty + "\n\n"
                + "Please process the order as soon as possible.\n\n"
                + "— Rural Team";
        send(sellerEmail, subject, body);
    }

    // ── Seller: withdrawal approved/rejected ──────────────────────────────────
    @Async
    public void sendWithdrawalUpdate(String sellerEmail, String sellerName,
                                     double amount, String status) {
        String subject = "Withdrawal Request " + status + " — Rural";
        String body = "Dear " + sellerName + ",\n\n"
                + "Your withdrawal request of ৳" + String.format("%.2f", amount)
                + " has been " + status.toLowerCase() + ".\n\n"
                + "— Rural Team";
        send(sellerEmail, subject, body);
    }

    // ── Password reset OTP ────────────────────────────────────────────────────
    @Async
    public void sendPasswordResetOtp(String toEmail, String name, String otp) {
        String subject = "Your Rural Password Reset Code";
        String body = "Dear " + (name != null && !name.trim().isEmpty() ? name : "User") + ",\n\n"
                + "We received a request to reset your Rural account password.\n\n"
                + "Your OTP code is: " + otp + "\n\n"
                + "This code will expire in 10 minutes. If you did not request this, "
                + "please ignore this email — your password will remain unchanged.\n\n"
                + "— Rural Team";
        send(toEmail, subject, body);
    }

    // ── Internal send ─────────────────────────────────────────────────────────
    private void send(String to, String subject, String body) {
        if (!mailEnabled || mailSender == null) {
            System.out.println("[EMAIL-DISABLED] To: " + to + " | " + subject);
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
            System.err.println("[EMAIL-ERROR] Failed to send to " + to + ": " + e.getMessage());
        }
    }
}
