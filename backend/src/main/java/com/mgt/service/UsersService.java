package com.mgt.service;

import com.mgt.dao.UsersDAO;
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
public class UsersService {

    @Autowired
    UsersDAO usersDAO;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:${user.home}/rural-uploads}")
    private String uploadDir;

    public Map<String, Object> register(Users user) {
        Map<String, Object> result = new HashMap<>();

        if (user == null) {
            result.put("success", false);
            result.put("message", "User information is required.");
            return result;
        }

        if (isBlank(user.getName())) {
            result.put("success", false);
            result.put("message", "Name is required.");
            return result;
        }

        if (isBlank(user.getEmail())) {
            result.put("success", false);
            result.put("message", "Email is required.");
            return result;
        }

        if (isBlank(user.getPassword()) || user.getPassword().trim().length() < 6) {
            result.put("success", false);
            result.put("message", "Password must be at least 6 characters.");
            return result;
        }

        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        if (user.getPhone() != null) user.setPhone(user.getPhone().trim());
        if (user.getAddress() != null) user.setAddress(user.getAddress().trim());

        if (usersDAO.emailExists(user.getEmail())) {
            result.put("success", false);
            result.put("message", "This email is already registered.");
            return result;
        }

        String code = "USR-" + java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", (int)(Math.random() * 9000) + 1000);
        user.setUser_code(code);

        // Public registration must never accept privileged roles from the client.
        user.setRole("customer");

        // Password BCrypt hash করে save হবে।
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));

        usersDAO.save(user);

        result.put("success", true);
        result.put("message", "Registration successful!");
        result.put("user", toUserData(user));
        return result;
    }

    public Map<String, Object> login(String identifier, String password) {
        Map<String, Object> result = new HashMap<>();

        if (isBlank(identifier) || isBlank(password)) {
            result.put("success", false);
            result.put("message", "Email/phone and password are required.");
            return result;
        }

        Users user = usersDAO.findByEmailOrPhone(identifier.trim());

        if (user == null || !passwordMatchesAndMigrateIfNeeded(user, password.trim())) {
            result.put("success", false);
            result.put("message", "Invalid email/phone or password.");
            return result;
        }

        result.put("success", true);
        result.put("message", "Login successful!");
        result.put("user", toUserData(user));
        result.put("token", UUID.randomUUID().toString());
        return result;
    }

    /**
     * Existing DB-তে যদি plain-text password থাকে, login successful হলে সেটাকে
     * automatically BCrypt hash-এ migrate করে দেবে।
     */
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

        // Old/plain password support for previous database records
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

    private String normalizeRole(String role) {
        if (isBlank(role)) return "customer";
        String r = role.trim().toLowerCase();
        if ("seller".equals(r)) return "vendor";
        if ("vendor".equals(r)) return "vendor";
        if ("admin".equals(r)) return "admin";
        return "customer";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<Users> getAll() { return usersDAO.getAll(); }

    public Users getById(long id) { return usersDAO.getById(id); }

    public Map<String, Object> profile(long id) {
        Users user = usersDAO.getById(id);
        Map<String, Object> result = new HashMap<>();

        if (user == null) {
            result.put("success", false);
            result.put("message", "User not found.");
            return result;
        }

        result.put("success", true);
        result.put("user", toUserData(user));
        return result;
    }

    public Map<String, Object> updateProfile(long id, String name, String email, String phone,
                                             String address, String password, MultipartFile photo) {
        Map<String, Object> result = new HashMap<>();
        Users existing = usersDAO.getById(id);

        if (existing == null) {
            result.put("success", false);
            result.put("message", "User not found.");
            return result;
        }

        if (isBlank(email)) {
            result.put("success", false);
            result.put("message", "Email is required.");
            return result;
        }

        email = email.trim().toLowerCase();
        if (usersDAO.emailExistsForOtherUser(email, id)) {
            result.put("success", false);
            result.put("message", "This email is already used by another user.");
            return result;
        }

        existing.setName(name == null ? existing.getName() : name.trim());
        existing.setEmail(email);
        existing.setPhone(phone == null ? null : phone.trim());
        existing.setAddress(address == null ? null : address.trim());
        if (!isBlank(password)) {
            existing.setPassword(passwordEncoder.encode(password.trim()));
        }

        if (photo != null && !photo.isEmpty()) {
            existing.setProfileImage(saveProfilePhoto(photo));
        }

        usersDAO.update(existing);
        result.put("success", true);
        result.put("message", "Profile updated successfully.");
        result.put("user", toUserData(existing));
        return result;
    }

    public void update(long id, Users user) {
        user.setId(id);
        usersDAO.update(user);
    }

    public Map<String, Object> delete(long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleted = usersDAO.delete(id);
            if (!deleted) {
                result.put("success", false);
                result.put("message", "User not found.");
                return result;
            }
            result.put("success", true);
            result.put("message", "User deleted successfully.");
            return result;
        } catch (Exception ex) {
            result.put("success", false);
            result.put("message", "User could not be deleted because related records exist. Please remove related orders/products first.");
            return result;
        }
    }

    private Map<String, Object> toUserData(Users user) {
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("id",       user.getId());
        userData.put("name",     user.getName());
        userData.put("email",    user.getEmail());
        userData.put("phone",    user.getPhone());
        userData.put("address",  user.getAddress());
        userData.put("role",     user.getRole());
        userData.put("userCode", user.getUser_code());
        userData.put("profileImage", user.getProfileImage());
        return userData;
    }

    private String saveProfilePhoto(MultipartFile photo) {
        try {
            Path profileDir = Paths.get(uploadDir, "profiles").toAbsolutePath().normalize();
            Files.createDirectories(profileDir);

            String originalName = photo.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }

            String fileName = "profile-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Files.write(profileDir.resolve(fileName), photo.getBytes());
            return "profiles/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Profile photo upload failed", e);
        }
    }
}
