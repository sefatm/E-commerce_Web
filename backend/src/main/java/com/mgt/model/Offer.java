package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "offer")
@Table(name = "offer")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    // BANNER, FLASH_SALE, SEASONAL
    @Column(name = "offer_type")
    private String offerType;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    // নির্দিষ্ট product এ offer (optional)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private AddProduct product;

    // নির্দিষ্ট category তে offer (optional)
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // ACTIVE অথবা INACTIVE
    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "banner_image")
    private String bannerImage;

    @Column(name = "created_at")
    private LocalDate createdAt;

    // ===== Getters & Setters =====
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }
    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
    public AddProduct getProduct() { return product; }
    public void setProduct(AddProduct product) { this.product = product; }
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBannerImage() { return bannerImage; }
    public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
