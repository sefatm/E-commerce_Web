package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

@Entity(name = "order_item")
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id")
    private long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_price")
    private Double totalPrice;

    // ========== Getters & Setters ==========

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
