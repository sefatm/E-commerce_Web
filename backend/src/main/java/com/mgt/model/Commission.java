package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "commission")
@Table(name = "commission", uniqueConstraints = @UniqueConstraint(name = "uk_commission_order_seller", columnNames = {"order_id", "seller_id"}))
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({"items"})
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private Double grossAmount = 0.0;
    private Double commissionRate = 10.0;
    private Double commissionAmount = 0.0;
    private Double sellerAmount = 0.0;
    private String status = "PENDING";
    private LocalDate createdAt;
    private LocalDate paidAt;
    private LocalDate payableAt;
    private LocalDate reversedAt;
    private Boolean creditedToWallet = false;

    @Column(length = 500)
    private String reversalReason;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public Double getGrossAmount() { return grossAmount; }
    public void setGrossAmount(Double grossAmount) { this.grossAmount = grossAmount; }

    public Double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(Double commissionRate) { this.commissionRate = commissionRate; }

    public Double getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(Double commissionAmount) { this.commissionAmount = commissionAmount; }

    public Double getSellerAmount() { return sellerAmount; }
    public void setSellerAmount(Double sellerAmount) { this.sellerAmount = sellerAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDate paidAt) { this.paidAt = paidAt; }
    public LocalDate getPayableAt() { return payableAt; }
    public void setPayableAt(LocalDate payableAt) { this.payableAt = payableAt; }
    public LocalDate getReversedAt() { return reversedAt; }
    public void setReversedAt(LocalDate reversedAt) { this.reversedAt = reversedAt; }
    public Boolean getCreditedToWallet() { return creditedToWallet; }
    public void setCreditedToWallet(Boolean creditedToWallet) { this.creditedToWallet = creditedToWallet; }
    public String getReversalReason() { return reversalReason; }
    public void setReversalReason(String reversalReason) { this.reversalReason = reversalReason; }
}
