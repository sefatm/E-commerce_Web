package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * DeliveryMethod — একটি নির্দিষ্ট zone-এ delivery করার পদ্ধতি।
 * যেমন: "Pathao Courier", "Sundarban", "In-house Delivery"
 */
@Entity
@Table(name = "delivery_method")
public class DeliveryMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;               // e.g. "Pathao Courier"

    private String carrier;            // e.g. "Pathao", "Sundarban", "Redx"

    private String description;

    // Delivery charge (টাকায়)
    @Column(nullable = false)
    private Double charge = 0.0;

    // Free shipping minimum order amount (null = no free shipping)
    private Double freeShippingAbove;

    // Estimated delivery time, e.g. "2-3 business days"
    private String estimatedDays;

    // flat_rate | free | pickup
    private String type = "flat_rate";

    // active | inactive
    private String status = "active";

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private ShippingZone zone;

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCharge() { return charge; }
    public void setCharge(Double charge) { this.charge = charge; }

    public Double getFreeShippingAbove() { return freeShippingAbove; }
    public void setFreeShippingAbove(Double freeShippingAbove) { this.freeShippingAbove = freeShippingAbove; }

    public String getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(String estimatedDays) { this.estimatedDays = estimatedDays; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public ShippingZone getZone() { return zone; }
    public void setZone(ShippingZone zone) { this.zone = zone; }
}
