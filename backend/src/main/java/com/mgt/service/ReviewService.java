package com.mgt.service;

import com.mgt.dao.AddProductDAO;
import com.mgt.dao.ReviewDAO;
import com.mgt.dao.UsersDAO;
import com.mgt.model.AddProduct;
import com.mgt.model.Review;
import com.mgt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReviewService {

    @Autowired private ReviewDAO reviewDAO;
    @Autowired private UsersDAO usersDAO;
    @Autowired private AddProductDAO productDAO;

    public Review create(Map<String, Object> body) {
        long customerId = Long.parseLong(body.get("customerId").toString());
        int productId   = Integer.parseInt(body.get("productId").toString());
        int rating      = Integer.parseInt(body.get("rating").toString());

        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be 1–5");

        if (reviewDAO.existsByCustomerAndProduct(customerId, productId))
            throw new IllegalStateException("You have already reviewed this product");

        Users customer   = usersDAO.getById(customerId);
        AddProduct product = productDAO.getById(productId);
        if (customer == null) throw new RuntimeException("Customer not found");
        if (product  == null) throw new RuntimeException("Product not found");

        Review review = new Review();
        review.setCustomer(customer);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(body.getOrDefault("comment", "").toString());
        review.setStatus("APPROVED");
        reviewDAO.save(review);
        return review;
    }

    public List<Review> getAll()                          { return reviewDAO.getAll(); }
    public List<Review> getByProduct(int productId)       { return reviewDAO.getByProductId(productId); }
    public List<Review> getByRating(int rating)           { return reviewDAO.getByRating(rating); }
    public List<Review> getByStatus(String status)        { return reviewDAO.getByStatus(status); }

    public void delete(long id) { reviewDAO.delete(id); }

    public Map<String, Object> getProductStats(int productId) {
        List<Review> reviews = reviewDAO.getByProductId(productId);
        Double avg = reviewDAO.getAverageRatingByProduct(productId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReviews", reviews.size());
        stats.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        // rating distribution 1..5
        Map<Integer, Long> dist = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int r = i;
            dist.put(r, reviews.stream().filter(rv -> rv.getRating() == r).count());
        }
        stats.put("distribution", dist);
        return stats;
    }

    public Map<String, Object> toResponse(Review r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("rating", r.getRating());
        m.put("comment", r.getComment());
        m.put("status", r.getStatus());
        m.put("createdAt", r.getCreatedAt());
        if (r.getProduct() != null) {
            Map<String, Object> p = new HashMap<>();
            p.put("id",    r.getProduct().getId());
            p.put("name",  r.getProduct().getName());
            p.put("image", r.getProduct().getImage());
            m.put("product", p);
        }
        if (r.getCustomer() != null) {
            Map<String, Object> c = new HashMap<>();
            c.put("id",       r.getCustomer().getId());
            c.put("username", r.getCustomer().getName());
            c.put("email",    r.getCustomer().getEmail());
            m.put("customer", c);
        }
        return m;
    }
}
