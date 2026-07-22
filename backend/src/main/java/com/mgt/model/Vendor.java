package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDate;

// ✅ BUG FIX #1 — CRITICAL: Hibernate Entity Conflict
// ─────────────────────────────────────────────────────────
// আগের সমস্যা:
//   Seller.java  → @Entity(name="seller") @Table(name="seller")
//   Vendor.java  → @Entity(name="vendor") @Table(name="seller")  ← SAME TABLE!
//
// ফলাফল: দুটো @Entity একই "seller" table কে map করছিল।
// Hibernate এ এটা SchemaConflictException অথবা silent data corruption ঘটায়।
// vendor এর data seller table এ save হচ্ছিল, আলাদা vendor table ব্যবহার হচ্ছিল না।
//
// Fix: @Table(name="vendor") দেওয়া হয়েছে — এখন vendor table ব্যবহার হবে
// ─────────────────────────────────────────────────────────
@Entity(name = "vendor")
@Table(name = "vendor") // ✅ FIXED: "seller" → "vendor"
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String shopName;

    @Column(length = 1000)
    private String shopDescription;
    private String shopLogo;
    private String phone;
    private String email;
    private String address;
    private String nidNo;
    private Double commissionRate = 10.0;
    private Double totalEarnings  = 0.0;
    private Integer totalOrders   = 0;
    private Integer totalProducts = 0;
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountName;
    private String bankBranch;
    private Double  rating      = 0.0;
    private Integer reviewCount = 0;
    private String status = "pending";
    private String rejectionReason;
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"role"})
    private Users user;

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getShopDescription() { return shopDescription; }
    public void setShopDescription(String s) { this.shopDescription = s; }
    public String getShopLogo() { return shopLogo; }
    public void setShopLogo(String shopLogo) { this.shopLogo = shopLogo; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNidNo() { return nidNo; }
    public void setNidNo(String nidNo) { this.nidNo = nidNo; }
    public Double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(Double r) { this.commissionRate = r; }
    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double e) { this.totalEarnings = e; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer o) { this.totalOrders = o; }
    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer p) { this.totalProducts = p; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String n) { this.bankAccountNumber = n; }
    public String getBankAccountName() { return bankAccountName; }
    public void setBankAccountName(String n) { this.bankAccountName = n; }
    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String r) { this.rejectionReason = r; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
}
