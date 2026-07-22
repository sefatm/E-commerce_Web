package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.DeliveryMethod;
import com.mgt.service.DeliveryMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping/methods")

public class DeliveryMethodController {

    @Autowired
    DeliveryMethodService methodService;

    // POST /shipping/methods?zoneId=1
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestBody DeliveryMethod method,
            @RequestParam Long zoneId) {
        try {
            if (method.getName() == null || method.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Method name is required"));
            }
            boolean created = methodService.create(method, zoneId);
            if (!created) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Zone not found: " + zoneId));
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Delivery method created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed: " + e.getMessage()));
        }
    }

    // GET /shipping/methods
    // GET /shipping/methods?zoneId=1
    // GET /shipping/methods?status=active
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) String status) {

        List<DeliveryMethod> methods;
        if (zoneId != null) {
            methods = methodService.getByZone(zoneId);
        } else if ("active".equals(status)) {
            methods = methodService.getActive();
        } else {
            methods = methodService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok("Methods fetched", methods));
    }

    // GET /shipping/methods/available?district=Dhaka
    // Checkout page থেকে call হবে — কোন district-এ কোন methods available
    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailable(
            @RequestParam(defaultValue = "") String district) {
        List<DeliveryMethod> methods = methodService.getAvailableForDistrict(district);
        return ResponseEntity.ok(ApiResponse.ok("Available methods", methods));
    }

    // GET /shipping/methods/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        DeliveryMethod method = methodService.getById(id);
        if (method == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Method not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Method fetched", method));
    }

    // PUT /shipping/methods/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestBody DeliveryMethod method) {
        boolean updated = methodService.update(id, method);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Method not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Method updated"));
    }

    // DELETE /shipping/methods/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = methodService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Method not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Method deleted"));
    }
}
