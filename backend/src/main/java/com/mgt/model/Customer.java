package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "customer")
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String password;

    @Column(length = 500)
    private String address;

    private String district;
    private String upazila;
    private String postalCode;

    // regular / vip
    private String type = "regular";

    // active / inactive
    private String status = "active";

    @Column(name = "created_at")
    private LocalDate createdAt;

    // ===== Getters & Setters =====
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getUpazila() { return upazila; }
    public void setUpazila(String upazila) { this.upazila = upazila; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
