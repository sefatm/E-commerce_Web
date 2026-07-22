package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.Vendor;
import com.mgt.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ✅ BUG FIX #4 — Spring MVC URL Routing Conflict
// ─────────────────────────────────────────────────────────
// আগের সমস্যা:
//   VendorController       → @RequestMapping("/vendor")
//   VendorPayoutController → @RequestMapping("/vendor/payout")
//
// Spring MVC তে GET /vendor/payout/getall request আসলে:
//   → VendorController এর @GetMapping("/{id}") তে route হয়
//   → id = "payout" হিসেবে parse হয়
//   → Long.parseLong("payout") → NumberFormatException!
//   → VendorPayoutController কখনো call হয় না
//
// Fix: VendorController → /vendors (plural)
//      VendorPayoutController → /vendors/payout
//      Frontend vendor.service.ts → base URL /vendors
// ─────────────────────────────────────────────────────────
@RestController
@RequestMapping("/vendors") // ✅ FIXED: "/vendor" → "/vendors"
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class VendorController {

    @Autowired
    VendorService vendorService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody Vendor vendor) {
        try {
            if (vendor.getName() == null || vendor.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Vendor name is required"));
            }
            vendorService.create(vendor);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Vendor registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<Vendor> vendors;
        if (search != null && !search.trim().isEmpty())      vendors = vendorService.search(search);
        else if (status != null && !status.trim().isEmpty()) vendors = vendorService.getByStatus(status);
        else                                          vendors = vendorService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Vendors fetched", vendors));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        return ResponseEntity.ok(ApiResponse.ok("Stats fetched", vendorService.getStats()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        Vendor vendor = vendorService.getById(id);
        if (vendor == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Vendor fetched", vendor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Vendor vendor) {
        boolean updated = vendorService.update(id, vendor);
        if (!updated)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Vendor updated"));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Long id) {
        boolean done = vendorService.approve(id);
        if (!done)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Vendor approved"));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse> suspend(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        boolean done = vendorService.suspend(id, reason);
        if (!done)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Vendor suspended"));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        boolean done = vendorService.reject(id, reason);
        if (!done)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Vendor application rejected"));
    }

    @PatchMapping("/{id}/commission")
    public ResponseEntity<ApiResponse> updateCommission(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        Double rate = body.get("rate");
        if (rate == null || rate < 0 || rate > 100)
            return ResponseEntity.badRequest().body(ApiResponse.error("rate must be between 0 and 100"));
        boolean done = vendorService.updateCommission(id, rate);
        if (!done)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Commission updated to " + rate + "%"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = vendorService.delete(id);
        if (!deleted)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Vendor deleted"));
    }
}
