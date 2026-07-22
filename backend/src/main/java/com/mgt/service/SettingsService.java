package com.mgt.service;

import com.mgt.dao.SettingsDAO;
import com.mgt.dao.UsersDAO;
import com.mgt.model.Settings;
import com.mgt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class SettingsService {

    @Autowired 
    SettingsDAO settingsDAO;

    @Autowired
    UsersDAO    usersDAO;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:${user.home}/rural-uploads}")
    private String uploadDir;

    public Map<String, Object> getAll() {
        List<Settings> all = settingsDAO.getAll();

        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        for (Settings s : all) {
            grouped.computeIfAbsent(s.getSection(), k -> new LinkedHashMap<>())
                   .put(s.getSettingKey(), parseValue(s.getSettingValue()));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("site",         grouped.getOrDefault("site",         getDefaultSite()));
        result.put("profile",      grouped.getOrDefault("profile",      getDefaultProfile()));
        result.put("payment",      grouped.getOrDefault("payment",      getDefaultPayment()));
        result.put("shipping",     grouped.getOrDefault("shipping",     getDefaultShipping()));
        result.put("notification", grouped.getOrDefault("notification", getDefaultNotification()));
        return result;
    }

    public void save(String section, Map<String, Object> data) {
        data.forEach((key, value) ->
            settingsDAO.upsert(section, key, value != null ? value.toString() : "")
        );
    }

    public String saveAdminProfilePhoto(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is required.");
        }

        String contentType = photo.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        try {
            Path adminDir = Paths.get(uploadDir, "admin").toAbsolutePath().normalize();
            Files.createDirectories(adminDir);

            String originalName = photo.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName = "admin-profile-" + System.currentTimeMillis() + "-" +
                    UUID.randomUUID().toString().substring(0, 8) + ext;
            Files.write(adminDir.resolve(fileName), photo.getBytes());

            String savedPath = "admin/" + fileName;
            settingsDAO.upsert("profile", "profileImage", savedPath);
            return savedPath;
        } catch (IOException e) {
            throw new RuntimeException("Admin profile photo upload failed", e);
        }
    }

    public Map<String, Object> changeAdminPassword(String currentPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        if (isBlank(currentPassword) || isBlank(newPassword)) {
            result.put("success", false);
            result.put("message", "Current password and new password are required.");
            return result;
        }

        if (newPassword.trim().length() < 6) {
            result.put("success", false);
            result.put("message", "New password must be at least 6 characters.");
            return result;
        }

        Users admin = findSettingsAdminUser();
        if (admin == null) {
            result.put("success", false);
            result.put("message", "Admin user not found.");
            return result;
        }

        if (!passwordMatchesAndMigrateIfNeeded(admin, currentPassword.trim())) {
            result.put("success", false);
            result.put("message", "Current password is incorrect.");
            return result;
        }

        admin.setPassword(passwordEncoder.encode(newPassword.trim()));
        usersDAO.update(admin);

        result.put("success", true);
        result.put("message", "Password changed successfully.");
        return result;
    }

    private Users findSettingsAdminUser() {
        // 1) Settings profile email থাকলে সেই user খুঁজবে
        try {
            Settings profileEmail = settingsDAO.getByKey("profile", "email");
            if (profileEmail != null && !isBlank(profileEmail.getSettingValue())) {
                Users byEmail = usersDAO.findByEmail(profileEmail.getSettingValue().trim());
                if (byEmail != null) return byEmail;
            }
        } catch (Exception ignored) {}

        // 2) admin role-এর user
        Users admin = usersDAO.getAll().stream()
                .filter(u -> "admin".equalsIgnoreCase(u.getRole()))
                .findFirst().orElse(null);
        if (admin != null) return admin;

        // 3) fallback: DB-তে user থাকলে প্রথম user (পুরনো DB compatibility)
        List<Users> users = usersDAO.getAll();
        return users.isEmpty() ? null : users.get(0);
    }

    private boolean passwordMatchesAndMigrateIfNeeded(Users user, String rawPassword) {
        String storedPassword = user.getPassword();
        if (isBlank(storedPassword) || rawPassword == null) return false;

        if (isBCrypt(storedPassword)) {
            try {
                return passwordEncoder.matches(rawPassword, storedPassword);
            } catch (Exception e) {
                return false;
            }
        }

        // Old/plain text password support: match হলে BCrypt hash করে migrate করবে
        if (storedPassword.equals(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            usersDAO.update(user);
            return true;
        }

        return false;
    }

    private boolean isBCrypt(String password) {
        return password != null &&
                (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Object parseValue(String value) {
        if (value == null) return null;
        if ("true".equalsIgnoreCase(value))  return true;
        if ("false".equalsIgnoreCase(value)) return false;
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) {}
        try { return Double.parseDouble(value); } catch (NumberFormatException ignored) {}
        return value;
    }

    private Map<String, Object> getDefaultSite() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("siteName",    "Rural");
        m.put("siteTagline", "Your one-stop online shopping destination in Bangladesh.");
        m.put("contactEmail","support@rural.com.bd");
        m.put("contactPhone","+880 1700-000000");
        m.put("address",     "Dhaka, Bangladesh");
        m.put("currency",    "BDT");
        m.put("timezone",    "Asia/Dhaka");
        m.put("maintenanceMode", false);
        return m;
    }

    private Map<String, Object> getDefaultProfile() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name",  "Admin");
        m.put("email", "admin@rural.com.bd");
        m.put("phone", "");
        m.put("profileImage", "");
        return m;
    }

    private Map<String, Object> getDefaultPayment() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("codEnabled",   true);
        m.put("bkashEnabled", false);
        m.put("bkashNumber",  "");
        m.put("nagadEnabled", false);
        m.put("nagadNumber",  "");
        m.put("bankEnabled",  false);
        m.put("bankName",     "");
        m.put("bankAccount",  "");
        m.put("bankBranch",   "");
        return m;
    }

    private Map<String, Object> getDefaultShipping() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("freeShippingEnabled",   true);
        m.put("freeShippingThreshold", 1000);
        m.put("defaultShippingFee",    60);
        m.put("expressFee",            120);
        m.put("dhakaFee",              50);
        m.put("outsideDhakaFee",       100);
        return m;
    }

    private Map<String, Object> getDefaultNotification() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("emailOnNewOrder",    true);
        m.put("emailOnOrderStatus", true);
        m.put("emailOnLowStock",    true);
        m.put("lowStockThreshold",  5);
        m.put("smsOnNewOrder",      false);
        m.put("smsNumber",          "");
        return m;
    }
}
