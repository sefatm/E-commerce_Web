package com.mgt.controller;

import com.mgt.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/report")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class ReportController {

    @Autowired
    ReportService reportService;

    // Sales Report — date range দিয়ে filter
    // GET /report/sales?from=2026-01-01&to=2026-04-30
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesReport(
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(reportService.getSalesReport(from, to));
    }

    // Revenue Report — monthly breakdown last 6 months
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport() {
        return ResponseEntity.ok(reportService.getRevenueReport());
    }

    // Product Report — best selling products
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductReport() {
        return ResponseEntity.ok(reportService.getProductReport());
    }

    // Customer Report — customer spending breakdown
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomerReport() {
        return ResponseEntity.ok(reportService.getCustomerReport());
    }
}
