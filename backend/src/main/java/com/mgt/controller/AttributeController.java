package com.mgt.controller;

import com.mgt.service.ProductAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/attributes")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class AttributeController {

    @Autowired
    private ProductAttributeService attributeService;

    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(attributeService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(attributeService.create(body));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(attributeService.update(id, body));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        attributeService.delete(id);
        return ResponseEntity.ok("Attribute deleted");
    }

    @GetMapping("/{id}/values")
    public ResponseEntity<?> getValues(@PathVariable long id) {
        return ResponseEntity.ok(attributeService.getValuesList(id));
    }
}
