package com.mgt.controller;

import com.mgt.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/settings")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class SettingsController {

    @Autowired
    SettingsService settingsService;

    // GET /settings/all — সব settings একসাথে লোড
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(settingsService.getAll());
    }

    // POST /settings/save — যেকোনো section save
    // Body: { "section": "site", "data": { ... } }
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, Object> body) {
        String section = (String) body.get("section");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) body.get("data");

        if (section == null || data == null) {
            return ResponseEntity.badRequest().body(response(false, "Section and data are required."));
        }

        settingsService.save(section, data);
        return ResponseEntity.ok(response(true, "Settings saved."));
    }

    @PostMapping("/profile-photo")
    public ResponseEntity<Map<String, Object>> uploadProfilePhoto(@RequestParam("photo") MultipartFile photo) {
        try {
            String profileImage = settingsService.saveAdminProfilePhoto(photo);
            Map<String, Object> result = response(true, "Profile photo uploaded.");
            result.put("profileImage", profileImage);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(response(false, e.getMessage()));
        }
    }

    // POST /settings/change-password
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> body) {
        String current = firstNonBlank(
                body.get("currentPassword"),
                body.get("oldPassword"),
                body.get("current_password")
        );
        String newPass = firstNonBlank(
                body.get("newPassword"),
                body.get("password"),
                body.get("new_password")
        );

        Map<String, Object> result = settingsService.changeAdminPassword(current, newPass);

        // Angular console-এ unnecessary 400 error avoid করার জন্য success=false হলেও 200 return করা হলো।
        // UI result.success/message দেখে proper message দেখাবে।
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

    private Map<String, Object> response(boolean success, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        return result;
    }
}
