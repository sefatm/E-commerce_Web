package com.mgt.controller;

import com.mgt.dao.UsersDAO;
import com.mgt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ⚠️ ONE-TIME USE ONLY.
 *
 * Database-এ আগে থেকে থাকা plain-text password গুলো BCrypt hash করার জন্য।
 *
 * Usage:
 *   POST http://localhost:8080/auth/migrate-passwords
 *   Body: { "adminSecret": "RURAL_MIGRATE_2025" }
 *
 * Migration সফল হওয়ার পর এই ফাইলটি delete করে দাও।
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class PasswordMigrationController {

    @Autowired private UsersDAO usersDAO;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String MIGRATION_SECRET = "RURAL_MIGRATE_2025";
    private static volatile boolean migrationDone = false;

    @PostMapping("/migrate-passwords")
    public ResponseEntity<Map<String, Object>> migratePasswords(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();

        if (migrationDone) {
            result.put("success", false);
            result.put("message", "Migration already completed in this session.");
            return ResponseEntity.badRequest().body(result);
        }

        if (!MIGRATION_SECRET.equals(body.get("adminSecret"))) {
            result.put("success", false);
            result.put("message", "Invalid migration secret.");
            return ResponseEntity.status(403).body(result);
        }

        List<Users> users = usersDAO.getAll();
        int migrated = 0;
        int skipped  = 0;

        for (Users user : users) {
            String pwd = user.getPassword();
            // BCrypt hash সবসময় $2a$, $2b$ বা $2y$ দিয়ে শুরু হয়
            if (pwd == null || pwd.startsWith("$2a$") || pwd.startsWith("$2b$") || pwd.startsWith("$2y$")) {
                skipped++;
                continue;
            }
            user.setPassword(passwordEncoder.encode(pwd));
            usersDAO.update(user);
            migrated++;
        }

        migrationDone = true;

        result.put("success", true);
        result.put("message", "Migration complete. Delete PasswordMigrationController.java now.");
        result.put("migrated", migrated);
        result.put("skipped", skipped);
        result.put("total", users.size());
        return ResponseEntity.ok(result);
    }
}
