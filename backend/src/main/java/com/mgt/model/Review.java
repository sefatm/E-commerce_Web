package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "review")
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"seller","category","brand","description","artisanStory","craftProcess"})
    private AddProduct product;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private Users customer;

    @Column(nullable = false)
    private int rating;   // 1–5

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(length = 30)
    private String status = "APPROVED"; // APPROVED | PENDING | REJECTED

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // ── Getters & Setters ──────────────────────────────────
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public AddProduct getProduct() { return product; }
    public void setProduct(AddProduct product) { this.product = product; }

    public Users getCustomer() { return customer; }
    public void setCustomer(Users customer) { this.customer = customer; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
