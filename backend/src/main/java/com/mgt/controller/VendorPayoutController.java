package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.VendorPayout;
import com.mgt.service.VendorPayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ✅ BUG FIX #5 — VendorPayoutController URL Fix
// ─────────────────────────────────────────────────────────
// VendorController /vendor → /vendors হওয়ার সাথে সামঞ্জস্য রাখতে
// এই controller এর path ও /vendor/payout → /vendors/payout করা হয়েছে
// Frontend vendor.service.ts এ: `${base}/payout/...` — base এখন /vendors
// ─────────────────────────────────────────────────────────
@RestController
@RequestMapping("/vendors/payout") // ✅ FIXED: "/vendor/payout" → "/vendors/payout"
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class VendorPayoutController {

    @Autowired
    VendorPayoutService payoutService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse> request(@RequestBody Map<String, Object> body) {
        try {
            Long vendorId = Long.parseLong(body.get("vendorId").toString());
            Double amount = Double.parseDouble(body.get("amount").toString());
            String method = body.getOrDefault("paymentMethod", "bank_transfer").toString();
            String note   = body.getOrDefault("note", "").toString();

            if (amount <= 0)
                return ResponseEntity.badRequest().body(ApiResponse.error("Amount must be greater than 0"));

            boolean created = payoutService.requestPayout(vendorId, amount, method, note);
            if (!created)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Vendor not found: " + vendorId));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Payout request created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid request: " + e.getMessage()));
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String status) {
        List<VendorPayout> list;
        if (vendorId != null)                         list = payoutService.getByVendor(vendorId);
        else if (status != null && !status.trim().isEmpty()) list = payoutService.getByStatus(status);
        else                                          list = payoutService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Payouts fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        VendorPayout payout = payoutService.getById(id);
        if (payout == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payout not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Payout fetched", payout));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approve(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String txRef = body != null ? body.getOrDefault("transactionRef", "") : "";
        String note  = body != null ? body.getOrDefault("note", "") : "";
        boolean done = payoutService.approvePayout(id, txRef, note);
        if (!done)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payout not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Payout approved and marked as paid"));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.getOrDefault("note", "") : "";
        boolean done = payoutService.rejectPayout(id, note);
        if (!done)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payout not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Payout rejected"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = payoutService.delete(id);
        if (!deleted)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Payout not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Payout deleted"));
    }
}
