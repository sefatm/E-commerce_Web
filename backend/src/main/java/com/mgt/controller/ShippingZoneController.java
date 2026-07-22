package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.ShippingZone;
import com.mgt.service.ShippingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping/zones")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class ShippingZoneController {

    @Autowired
    ShippingZoneService zoneService;

    // POST /shipping/zones
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody ShippingZone zone) {
        try {
            if (zone.getName() == null || zone.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Zone name is required"));
            }
            zoneService.create(zone);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Shipping zone created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed: " + e.getMessage()));
        }
    }

    // GET /shipping/zones
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(value = "status", required = false) String status) {
        List<ShippingZone> zones = (status != null && status.equals("active"))
                ? zoneService.getActive()
                : zoneService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Zones fetched", zones));
    }

    // GET /shipping/zones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        ShippingZone zone = zoneService.getById(id);
        if (zone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Zone not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Zone fetched", zone));
    }

    // PUT /shipping/zones/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestBody ShippingZone zone) {
        boolean updated = zoneService.update(id, zone);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Zone not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Zone updated"));
    }

    // DELETE /shipping/zones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = zoneService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Zone not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Zone deleted"));
    }
}
