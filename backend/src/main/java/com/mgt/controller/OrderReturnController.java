package com.mgt.controller;

import com.mgt.model.OrderReturn;
import com.mgt.service.OrderReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order/return")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class OrderReturnController {

    @Autowired
    OrderReturnService returnService;

    // Customer — return request পাঠানো
    @PostMapping("/request")
    public ResponseEntity<String> requestReturn(@RequestBody Map<String, Object> body) {
        try {
            long orderId = Long.parseLong(body.get("orderId").toString());
            String productName = (String) body.get("productName");
            String reason = (String) body.get("reason");
            String refundMethod = body.get("refundMethod") == null ? "" : body.get("refundMethod").toString();
            String refundAccountNumber = body.get("refundAccountNumber") == null ? "" : body.get("refundAccountNumber").toString();
            String refundAccountName = body.get("refundAccountName") == null ? "" : body.get("refundAccountName").toString();
            String refundBankName = body.get("refundBankName") == null ? "" : body.get("refundBankName").toString();
            String refundBranch = body.get("refundBranch") == null ? "" : body.get("refundBranch").toString();
            returnService.create(orderId, productName, reason, refundMethod, refundAccountNumber,
                    refundAccountName, refundBankName, refundBranch);
            return ResponseEntity.ok("Return request submitted");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getall")
    public List<OrderReturn> getAll() {
        return returnService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderReturn> getById(@PathVariable long id) {
        return ResponseEntity.ok(returnService.getById(id));
    }

    @GetMapping("/status/{status}")
    public List<OrderReturn> getByStatus(@PathVariable String status) {
        return returnService.getByStatus(status);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderReturn> getByOrder(@PathVariable long orderId) {
        return returnService.getByOrderId(orderId);
    }

    // Admin — Approve করা
    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approve(
            @PathVariable long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.getOrDefault("note", "") : "";
        String method = body != null ? body.getOrDefault("refundMethod", "") : "";
        Double amount = null;
        if (body != null && body.get("refundAmount") != null && !body.get("refundAmount").trim().isEmpty()) {
            amount = Double.parseDouble(body.get("refundAmount"));
        }
        try {
            returnService.approve(id, note, amount, method);
            return ResponseEntity.ok("Return approved");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> advanceStatus(@PathVariable long id, @RequestBody Map<String, String> body) {
        try {
            returnService.advanceStatus(id, body.get("status"), body.getOrDefault("note", ""),
                    body.getOrDefault("refundMethod", ""), body.getOrDefault("transactionRef", ""));
            return ResponseEntity.ok("Return/refund status updated");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Admin — Reject করা
    @PatchMapping("/{id}/reject")
    public ResponseEntity<String> reject(
            @PathVariable long id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.getOrDefault("note", "") : "";
        try {
            returnService.reject(id, note);
            return ResponseEntity.ok("Return rejected");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        returnService.delete(id);
        return ResponseEntity.ok("Return deleted");
    }
}
