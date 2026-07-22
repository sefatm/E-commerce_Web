package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "orders")
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Auto-generated order code like RURAL-20260420-0001
    @Column(name = "order_code", unique = true)
    private String orderCode;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    // PENDING, ACCEPTED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    @Column(name = "status")
    private String status = "PENDING";

    // COD, BKASH, NAGAD, ONLINE
    @Column(name = "payment_method")
    private String paymentMethod;

    // UNPAID, PAID, REFUNDED
    @Column(name = "payment_status")
    private String paymentStatus = "UNPAID";

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "total_amount")
    private Double totalAmount;

    // Coupon code used (nullable)
    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "order_note", length = 1000)
    private String orderNote;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "delivered_date")
    private LocalDate deliveredDate;

    @Column(name = "invoice_no", unique = true)
    private String invoiceNo;

    @Column(name = "scan_token", unique = true)
    private String scanToken;

    @Column(name = "cod_collected_at")
    private LocalDateTime codCollectedAt;

    @Column(name = "cod_verified_at")
    private LocalDateTime codVerifiedAt;

    @Column(name = "refund_status")
    private String refundStatus = "NOT_REQUESTED";

    // One order has many order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    // ========== Getters & Setters ==========

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(LocalDate deliveredDate) { this.deliveredDate = deliveredDate; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public String getScanToken() { return scanToken; }
    public void setScanToken(String scanToken) { this.scanToken = scanToken; }
    public LocalDateTime getCodCollectedAt() { return codCollectedAt; }
    public void setCodCollectedAt(LocalDateTime codCollectedAt) { this.codCollectedAt = codCollectedAt; }
    public LocalDateTime getCodVerifiedAt() { return codVerifiedAt; }
    public void setCodVerifiedAt(LocalDateTime codVerifiedAt) { this.codVerifiedAt = codVerifiedAt; }
    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
