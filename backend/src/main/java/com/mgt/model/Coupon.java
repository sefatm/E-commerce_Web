package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "coupon")
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    // PERCENTAGE অথবা FLAT
    @Column(name = "discount_type", nullable = false)
    private String discountType;

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "min_order_amount")
    private Double minOrderAmount;

    // PERCENTAGE এর জন্য maximum discount cap
    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // ACTIVE অথবা INACTIVE
    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDate createdAt;

    // ===== Getters & Setters =====
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }
    public Double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(Double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public Double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(Double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
