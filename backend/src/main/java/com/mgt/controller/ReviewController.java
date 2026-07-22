package com.mgt.controller;

import com.mgt.model.Review;
import com.mgt.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products/reviews")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // GET /products/reviews              — admin: all reviews (filter by rating/status)
    @GetMapping
    public List<Map<String, Object>> getAll(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status) {

        List<Review> reviews;
        if (rating != null && rating > 0)       reviews = reviewService.getByRating(rating);
        else if (status != null && !status.equals("all")) reviews = reviewService.getByStatus(status);
        else                                     reviews = reviewService.getAll();

        return reviews.stream().map(reviewService::toResponse).collect(Collectors.toList());
    }

    // GET /products/reviews/product/{id}  — public: reviews for one product
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getByProduct(@PathVariable int productId) {
        List<Map<String, Object>> list = reviewService.getByProduct(productId)
                .stream().map(reviewService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // GET /products/reviews/product/{id}/stats
    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable int productId) {
        return ResponseEntity.ok(reviewService.getProductStats(productId));
    }

    // POST /products/reviews
    // body: { customerId, productId, rating, comment }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Review review = reviewService.create(body);
            return ResponseEntity.ok(reviewService.toResponse(review));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // DELETE /products/reviews/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        reviewService.delete(id);
        return ResponseEntity.ok("Review deleted");
    }
}
