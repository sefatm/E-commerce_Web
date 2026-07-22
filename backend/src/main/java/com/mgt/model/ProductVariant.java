package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity(name = "product_variant")
@Table(name = "product_variant")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"seller","category","brand","description","artisanStory","craftProcess","image"})
    private AddProduct product;

    @Column(length = 100)
    private String attributeName;  // e.g. "Color"

    @Column(length = 100)
    private String attributeValue; // e.g. "Red"

    private Double priceAdjustment = 0.0; // added on top of base price

    private Integer stock = 0;

    @Column(length = 100)
    private String sku;

    // ── Getters & Setters ──────────────────────────────────
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public AddProduct getProduct() { return product; }
    public void setProduct(AddProduct product) { this.product = product; }

    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public String getAttributeValue() { return attributeValue; }
    public void setAttributeValue(String attributeValue) { this.attributeValue = attributeValue; }

    public Double getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(Double priceAdjustment) { this.priceAdjustment = priceAdjustment; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
}
