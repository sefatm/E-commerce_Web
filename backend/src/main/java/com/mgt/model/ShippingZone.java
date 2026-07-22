package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * ShippingZone — একটি geographic area (ঢাকা, চট্টগ্রাম, সারাদেশ ইত্যাদি)
 * প্রতিটি zone-এ multiple DeliveryMethod থাকতে পারে।
 */
@Entity
@Table(name = "shipping_zone")
public class ShippingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;             // e.g. "ঢাকা সিটি", "সারাদেশ"

    private String description;

    // Comma-separated district/city names
    @Column(length = 1000)
    private String regions;          // e.g. "Dhaka, Narayanganj, Gazipur"

    // active | inactive
    private String status = "active";

    private LocalDate createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<DeliveryMethod> methods;

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRegions() { return regions; }
    public void setRegions(String regions) { this.regions = regions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public List<DeliveryMethod> getMethods() { return methods; }
    public void setMethods(List<DeliveryMethod> methods) { this.methods = methods; }
}
