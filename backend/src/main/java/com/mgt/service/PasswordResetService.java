package com.mgt.service;

import com.mgt.dao.PasswordResetOtpDAO;
import com.mgt.dao.UsersDAO;
import com.mgt.model.PasswordResetOtp;
import com.mgt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired private UsersDAO usersDAO;
    @Autowired private PasswordResetOtpDAO otpDAO;
    @Autowired private EmailNotificationService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_REQUESTS_PER_WINDOW = 5;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 15;

    /**
     * STEP 1: Email-এ OTP পাঠাও।
     * Security: email exist করুক বা না করুক, একই generic message — account enumeration prevent করার জন্য।
     */
    public Map<String, Object> requestOtp(String email) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Email is required.");
            return result;
        }
        email = email.trim().toLowerCase();

        Users user = usersDAO.findByEmail(email);

        // Rate limiting — same email-এ বারবার spam request রোধ
        long recentRequests = otpDAO.countRecentRequests(email, LocalDateTime.now().minusMinutes(RATE_LIMIT_WINDOW_MINUTES));
        if (recentRequests >= MAX_REQUESTS_PER_WINDOW) {
            result.put("success", false);
            result.put("message", "Too many requests. Please try again after some time.");
            return result;
        }

        if (user != null) {
            // পুরনো unused OTP invalidate করো
            otpDAO.invalidateOldOtps(email);

            String otp = generateOtp();

            PasswordResetOtp record = new PasswordResetOtp();
            record.setEmail(email);
            record.setOtp(otp);
            record.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            record.setUsed(false);
            otpDAO.save(record);

            emailService.sendPasswordResetOtp(email, user.getName(), otp);
        }
        // user না থাকলেও same success message — email enumeration prevent

        result.put("success", true);
        result.put("message", "If an account exists with this email, a verification code has been sent.");
        return result;
    }

    /**
     * STEP 2: OTP verify করো (password change করার আগে frontend এটা call করে
     * যাতে user reset form-এ যাওয়ার আগে OTP সঠিক কিনা জানতে পারে)।
     */
    public Map<String, Object> verifyOtp(String email, String otp) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || otp == null || otp.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Email and OTP are required.");
            return result;
        }
        email = email.trim().toLowerCase();

        PasswordResetOtp record = otpDAO.findValidOtp(email, otp.trim());
        if (record == null) {
            result.put("success", false);
            result.put("message", "Invalid or expired OTP. Please request a new code.");
            return result;
        }

        result.put("success", true);
        result.put("message", "OTP verified.");
        return result;
    }

    /**
     * STEP 3: OTP + নতুন password দিয়ে password reset করো।
     * OTP আবার validate হয় (security — verify ও reset আলাদা request হতে পারে)।
     */
    public Map<String, Object> resetPassword(String email, String otp, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        if (email == null || otp == null || newPassword == null || newPassword.trim().length() < 6) {
            result.put("success", false);
            result.put("message", "Password must be at least 6 characters.");
            return result;
        }
        email = email.trim().toLowerCase();

        PasswordResetOtp record = otpDAO.findValidOtp(email, otp.trim());
        if (record == null) {
            result.put("success", false);
            result.put("message", "Invalid or expired OTP. Please request a new code.");
            return result;
        }

        Users user = usersDAO.findByEmail(email);
        if (user == null) {
            result.put("success", false);
            result.put("message", "Account not found.");
            return result;
        }

        // ✅ BCrypt hash করে নতুন password store করো
        user.setPassword(passwordEncoder.encode(newPassword));
        usersDAO.update(user);

        // OTP একবার use হলে invalidate
        record.setUsed(true);
        otpDAO.update(record);

        result.put("success", true);
        result.put("message", "Password reset successfully. You can now log in with your new password.");
        return result;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit, 100000-999999
        return String.valueOf(otp);
    }
}
