package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "wishlist")
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password"})
    private Users user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private AddProduct product;

    private LocalDate createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public AddProduct getProduct() { return product; }
    public void setProduct(AddProduct product) { this.product = product; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
