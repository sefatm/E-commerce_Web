package com.mgt.controller;

import com.mgt.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/variants")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class VariantController {

    @Autowired
    private ProductVariantService variantService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getByProduct(@PathVariable int productId) {
        return ResponseEntity.ok(variantService.getByProduct(productId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(variantService.create(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(variantService.update(id, body));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        variantService.delete(id);
        return ResponseEntity.ok("Variant deleted");
    }
}
