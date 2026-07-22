package com.mgt.controller;

import com.mgt.model.Customer;
import com.mgt.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class CustomerController {

    @Autowired
    CustomerService customerService;

    // নতুন customer তৈরি
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody Customer customer) {
        try {
            customerService.create(customer);
            return ResponseEntity.ok("Customer created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // সব customer দেখা (Admin list)
    @GetMapping("/getall")
    public List<Customer> getAll() {
        return customerService.getAll();
    }

    // একটা customer দেখা
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable long id) {
        Customer c = customerService.getById(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody Customer customer) {
        customerService.update(id, customer);
        return ResponseEntity.ok("Customer updated");
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        customerService.delete(id);
        return ResponseEntity.ok("Customer deleted");
    }

    // Dashboard stats এর জন্য
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> stats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total",  customerService.countTotal());
        stats.put("active", customerService.countActive());
        stats.put("vip",    customerService.countVip());
        return ResponseEntity.ok(stats);
    }
}
