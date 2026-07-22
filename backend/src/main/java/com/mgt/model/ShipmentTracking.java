package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ShipmentTracking — একটি order-এর shipment tracking information।
 * Order place হলে এই record তৈরি হয়।
 */
@Entity
@Table(name = "shipment_tracking")
public class ShipmentTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Order ID (orders table-এর reference — future order module এর সাথে connect হবে)
    @Column(nullable = false)
    private Long orderId;

    // Tracking number (courier company দেওয়া)
    private String trackingNumber;

    // Courier/carrier name
    private String carrier;           // e.g. "Pathao", "Sundarban"

    // Current status
    // pending | picked_up | in_transit | out_for_delivery | delivered | failed | returned
    private String status = "pending";

    // Shipping address (denormalized for immutability)
    private String recipientName;
    private String recipientPhone;

    @Column(length = 500)
    private String shippingAddress;

    private String city;
    private String district;

    // Shipping charge applied on this order
    private Double shippingCharge;

    // Estimated delivery date
    private LocalDate estimatedDelivery;

    // Actual delivery date
    private LocalDate deliveredAt;

    // Internal notes
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "method_id")
    private DeliveryMethod method;

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Double getShippingCharge() { return shippingCharge; }
    public void setShippingCharge(Double shippingCharge) { this.shippingCharge = shippingCharge; }

    public LocalDate getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDate estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public LocalDate getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDate deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public DeliveryMethod getMethod() { return method; }
    public void setMethod(DeliveryMethod method) { this.method = method; }
}
