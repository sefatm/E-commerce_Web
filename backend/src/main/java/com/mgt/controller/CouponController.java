package com.mgt.controller;

import com.mgt.model.Coupon;
import com.mgt.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class CouponController {

    @Autowired
    CouponService couponService;

    // ===== ADMIN =====

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody Coupon coupon) {
        couponService.create(coupon);
        return ResponseEntity.ok("Coupon created successfully");
    }

    @GetMapping("/getall")
    public List<Coupon> getAll() {
        return couponService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getById(@PathVariable long id) {
        Coupon c = couponService.getById(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        couponService.update(coupon);
        return ResponseEntity.ok("Coupon updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        couponService.delete(id);
        return ResponseEntity.ok("Coupon deleted");
    }

    // ===== CUSTOMER / CHECKOUT =====

    /**
     * Checkout থেকে coupon validate করা
     * Request: { "code": "SAVE20", "orderTotal": 1500.0 }
     * Response: { valid, discountAmount, finalTotal, message, couponId }
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        Double orderTotal = Double.parseDouble(body.get("orderTotal").toString());
        Map<String, Object> result = couponService.validateAndApply(code, orderTotal);
        return ResponseEntity.ok(result);
    }

    // Order confirm হলে usedCount বাড়ানো
    @PostMapping("/mark-used/{couponId}")
    public ResponseEntity<String> markUsed(@PathVariable long couponId) {
        couponService.markCouponAsUsed(couponId);
        return ResponseEntity.ok("Coupon usage recorded");
    }
}
