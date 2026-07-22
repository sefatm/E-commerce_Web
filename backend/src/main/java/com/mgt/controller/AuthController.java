package com.mgt.controller;

import com.mgt.model.Users;
import com.mgt.service.PasswordResetService;
import com.mgt.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class AuthController {

    @Autowired
    UsersService usersService;

    @Autowired
    PasswordResetService passwordResetService;

    /**
     * POST /auth/register
     * Body: { name, email, phone, password, address, role }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Users user) {
        Map<String, Object> result = usersService.register(user);
        boolean success = (boolean) result.get("success");
        return success
                ? ResponseEntity.status(201).body(result)
                : ResponseEntity.badRequest().body(result);
    }

    /**
     * POST /auth/login
     * Body supported:
     *   { email, password }
     *   { phone, password }
     *   { emailOrPhone, password }
     *
     * Important:
     * Invalid credential response is returned as HTTP 200 with success=false.
     * This prevents Angular console POST 401 noise and lets the UI show the message.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String identifier = firstNonBlank(
                body.get("email"),
                body.get("phone"),
                body.get("emailOrPhone"),
                body.get("identifier"),
                body.get("username")
        );
        String password = body.get("password");

        if (identifier == null || password == null || password.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Email/phone and password are required.");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> result = usersService.login(identifier, password);
        return ResponseEntity.ok(result);
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    /**
     * GET /auth/users — Admin: সব user দেখা
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(usersService.getAll());
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> profile(@PathVariable long id) {
        Map<String, Object> result = usersService.profile(id);
        boolean success = (boolean) result.get("success");
        return success ? ResponseEntity.ok(result) : ResponseEntity.status(404).body(result);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        Map<String, Object> result = usersService.updateProfile(id, name, email, phone, address, password, photo);
        boolean success = (boolean) result.get("success");
        return success ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    /**
     * DELETE /auth/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable long id) {
        Map<String, Object> result = usersService.delete(id);
        // 500 error avoid করার জন্য failed delete-ও JSON response হিসেবে return করা হচ্ছে।
        return ResponseEntity.ok(result);
    }

    // ================= FORGOT PASSWORD (OTP via email) =================

    /**
     * STEP 1: POST /auth/forgot-password
     * Body: { email }
     * Email-এ ৬-digit OTP পাঠায় (10 মিনিট valid)।
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
        Map<String, Object> result = passwordResetService.requestOtp(body.get("email"));
        boolean success = (boolean) result.get("success");
        return success ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    /**
     * STEP 2: POST /auth/verify-otp
     * Body: { email, otp }
     * OTP সঠিক কিনা যাচাই করে — password reset form দেখানোর আগে।
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        Map<String, Object> result = passwordResetService.verifyOtp(body.get("email"), body.get("otp"));
        boolean success = (boolean) result.get("success");
        return success ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }

    /**
     * STEP 3: POST /auth/reset-password
     * Body: { email, otp, newPassword }
     * OTP verify করে password update করে।
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> body) {
        Map<String, Object> result = passwordResetService.resetPassword(
                body.get("email"), body.get("otp"), body.get("newPassword"));
        boolean success = (boolean) result.get("success");
        return success ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
}
