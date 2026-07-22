package com.mgt.controller;

import com.mgt.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getByUser(@PathVariable long userId) {
        return wishlistService.getByUserId(userId)
                .stream()
                .map(wishlistService::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<Map<String, Object>> add(@PathVariable long userId, @PathVariable int productId) {
        return ResponseEntity.ok(wishlistService.toResponse(wishlistService.add(userId, productId)));
    }

    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<String> remove(@PathVariable long userId, @PathVariable int productId) {
        wishlistService.remove(userId, productId);
        return ResponseEntity.ok("Removed from wishlist");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeById(@PathVariable long id) {
        wishlistService.removeById(id);
        return ResponseEntity.ok("Removed from wishlist");
    }

    @GetMapping("/user/{userId}/product/{productId}/exists")
    public Map<String, Object> exists(@PathVariable long userId, @PathVariable int productId) {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("exists", wishlistService.exists(userId, productId));
        return result;
    }
}
