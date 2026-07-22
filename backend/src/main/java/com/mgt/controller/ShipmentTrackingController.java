package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.ShipmentTracking;
import com.mgt.service.ShipmentTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shipping/tracking")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class ShipmentTrackingController {

    @Autowired
    ShipmentTrackingService trackingService;

    // POST /shipping/tracking?methodId=1
    // নতুন shipment তৈরি করো (order place হলে)
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestBody ShipmentTracking tracking,
            @RequestParam(required = false) Long methodId) {
        try {
            if (tracking.getOrderId() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("orderId is required"));
            }
            ShipmentTracking saved = trackingService.create(tracking, methodId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Shipment created", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed: " + e.getMessage()));
        }
    }

    // GET /shipping/tracking
    // GET /shipping/tracking?status=in_transit
    // GET /shipping/tracking?search=keyword
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        List<ShipmentTracking> list;
        if (search != null && !search.trim().isEmpty()) {
            list = trackingService.search(search);
        } else if (status != null && !status.trim().isEmpty()) {
            list = trackingService.getByStatus(status);
        } else {
            list = trackingService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok("Tracking list fetched", list));
    }

    // GET /shipping/tracking/stats
    // Dashboard summary
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        Map<String, Object> stats = trackingService.getStats();
        return ResponseEntity.ok(ApiResponse.ok("Stats fetched", stats));
    }

    // GET /shipping/tracking/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        ShipmentTracking tracking = trackingService.getById(id);
        if (tracking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Shipment not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Shipment fetched", tracking));
    }

    // GET /shipping/tracking/order/{orderId}
    // Order ID দিয়ে tracking খোঁজো
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getByOrderId(@PathVariable Long orderId) {
        ShipmentTracking tracking = trackingService.getByOrderId(orderId);
        if (tracking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No shipment for order: " + orderId));
        }
        return ResponseEntity.ok(ApiResponse.ok("Shipment fetched", tracking));
    }

    // GET /shipping/tracking/track/{trackingNumber}
    // Customer-facing public tracking (login ছাড়া)
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ApiResponse> track(@PathVariable String trackingNumber) {
        ShipmentTracking tracking = trackingService.getByTrackingNumber(trackingNumber);
        if (tracking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Tracking number not found: " + trackingNumber));
        }
        return ResponseEntity.ok(ApiResponse.ok("Tracking info", tracking));
    }

    // PATCH /shipping/tracking/{id}/status
    // Status update: { "status": "in_transit" }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("status field required"));
        }

        // Valid statuses
        List<String> validStatuses = Arrays.asList(
                "pending", "picked_up", "in_transit",
                "out_for_delivery", "delivered", "failed", "returned");
        if (!validStatuses.contains(newStatus)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status: " + newStatus));
        }

        boolean updated = trackingService.updateStatus(id, newStatus);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Shipment not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Status updated to: " + newStatus));
    }

    // PUT /shipping/tracking/{id}
    // Full update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestBody ShipmentTracking tracking) {
        boolean updated = trackingService.update(id, tracking);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Shipment not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Shipment updated"));
    }

    // DELETE /shipping/tracking/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = trackingService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Shipment not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Shipment deleted"));
    }
}
