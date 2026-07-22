package com.mgt.controller;

import com.mgt.model.Order;
import com.mgt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class OrderController {

    @Autowired
    OrderService orderService;

    // Customer checkout endpoint
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        Order saved = orderService.create(order);
        return ResponseEntity.ok(saved);
    }

    // ===== ADMIN endpoints =====

    @GetMapping("/getall")
    public List<Order> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable long id) {
        Order order = orderService.getById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Order> getByCode(@PathVariable String code) {
        Order order = orderService.getByOrderCode(code);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    public List<Order> getByStatus(@PathVariable String status) {
        return orderService.getByStatus(status);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Order> getBySeller(@PathVariable long sellerId) {
        return orderService.getBySellerId(sellerId);
    }

    @GetMapping("/seller/{sellerId}/dashboard")
    public ResponseEntity<Map<String, Object>> sellerDashboard(@PathVariable long sellerId) {
        return ResponseEntity.ok(orderService.getSellerDashboard(sellerId));
    }

    @GetMapping("/search/{keyword}")
    public List<Order> search(@PathVariable String keyword) {
        return orderService.search(keyword);
    }

    // Status update: PENDING -> ACCEPTED -> PROCESSING -> SHIPPED -> DELIVERED, or CANCELLED before shipping
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            orderService.updateStatus(id, status);
            return ResponseEntity.ok("Status updated to " + status);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/scan-update")
    public ResponseEntity<String> scanUpdate(@PathVariable long id,
            @RequestParam(required = false) String token,
            @RequestBody Map<String, String> body) {
        try {
            orderService.scanUpdate(id, body.get("status"), body.getOrDefault("note", ""), token);
            return ResponseEntity.ok("Order step updated successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/cod-collected")
    public ResponseEntity<String> codCollected(@PathVariable long id) {
        try {
            orderService.markCodCollected(id);
            return ResponseEntity.ok("COD payment collection recorded");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/cod-verify")
    public ResponseEntity<String> verifyCod(@PathVariable long id) {
        try {
            orderService.verifyCodPayment(id);
            return ResponseEntity.ok("COD payment verified and seller settlement released");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody Order order) {
        order.setId(id);
        orderService.update(order);
        return ResponseEntity.ok("Order updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Order deleted");
    }

    // Analytics data
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> analytics() {
        return ResponseEntity.ok(orderService.getAnalytics());
    }
}
