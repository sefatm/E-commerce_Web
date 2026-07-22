package com.mgt.controller;

import com.mgt.model.SellerWithdraw;
import com.mgt.service.SellerWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller/withdraw")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class SellerWithdrawController {

    @Autowired
    private SellerWithdrawService withdrawService;

    @PostMapping("/request/{sellerId}")
    public ResponseEntity<?> request(@PathVariable long sellerId, @RequestBody SellerWithdraw request) {
        try {
            return ResponseEntity.ok(withdrawService.request(sellerId, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getall")
    public List<SellerWithdraw> getAll(@RequestParam(required = false) String status) {
        return status != null && !status.trim().isEmpty()
                ? withdrawService.getByStatus(status)
                : withdrawService.getAll();
    }

    @GetMapping("/seller/{sellerId}")
    public List<SellerWithdraw> getBySeller(@PathVariable long sellerId) {
        return withdrawService.getBySeller(sellerId);
    }

    @GetMapping("/seller/{sellerId}/available")
    public ResponseEntity<Double> getAvailableBalance(@PathVariable long sellerId) {
        return ResponseEntity.ok(withdrawService.getAvailableBalance(sellerId));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable long id, @RequestBody(required = false) Map<String, String> body) {
        String txRef = body != null ? body.getOrDefault("transactionRef", "") : "";
        String note = body != null ? body.getOrDefault("note", "") : "";
        boolean done = withdrawService.approve(id, txRef, note);
        return done ? ResponseEntity.ok("Withdraw paid") : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable long id, @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.getOrDefault("note", "") : "";
        boolean done = withdrawService.reject(id, note);
        return done ? ResponseEntity.ok("Withdraw rejected") : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        boolean done = withdrawService.delete(id);
        return done ? ResponseEntity.ok("Withdraw deleted") : ResponseEntity.notFound().build();
    }
}
