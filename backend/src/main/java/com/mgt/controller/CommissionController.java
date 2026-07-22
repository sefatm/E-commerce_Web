package com.mgt.controller;

import com.mgt.model.Commission;
import com.mgt.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commission")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    @GetMapping("/getall")
    public List<Commission> getAll() {
        return commissionService.getAll();
    }

    @GetMapping("/status/{status}")
    public List<Commission> getByStatus(@PathVariable String status) {
        return commissionService.getByStatus(status);
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return commissionService.globalSummary();
    }

    @GetMapping("/seller/{sellerId}")
    public List<Commission> getBySeller(@PathVariable long sellerId) {
        return commissionService.getBySellerId(sellerId);
    }

    @GetMapping("/seller/{sellerId}/summary")
    public Map<String, Object> sellerSummary(@PathVariable long sellerId) {
        return commissionService.sellerSummary(sellerId);
    }

    @PatchMapping("/{id}/mark-payable")
    public ResponseEntity<String> markPayable(@PathVariable long id) {
        try {
            boolean updated = commissionService.markPayable(id);
            return updated ? ResponseEntity.ok("Commission released to seller wallet") : ResponseEntity.notFound().build();
        } catch (RuntimeException ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<String> markPaid(@PathVariable long id) {
        boolean updated = commissionService.markPaid(id);
        return updated ? ResponseEntity.ok("Commission marked paid") : ResponseEntity.notFound().build();
    }
}
