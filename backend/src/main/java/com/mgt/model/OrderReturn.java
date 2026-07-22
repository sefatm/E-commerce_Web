package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "order_return")
@Table(name = "order_return")
public class OrderReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "reason", length = 1000)
    private String reason;

    // PENDING, APPROVED, REJECTED
    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "resolved_date")
    private LocalDate resolvedDate;

    @Column(name = "admin_note", length = 500)
    private String adminNote;

    @Column(name = "refund_amount")
    private Double refundAmount = 0.0;

    // PENDING, PROCESSED
    @Column(name = "refund_status")
    private String refundStatus = "PENDING";

    @Column(name = "refund_date")
    private LocalDate refundDate;

    @Column(name = "refund_method")
    private String refundMethod;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "refund_account_number", length = 80)
    private String refundAccountNumber;

    @Column(name = "refund_account_name", length = 120)
    private String refundAccountName;

    @Column(name = "refund_bank_name", length = 120)
    private String refundBankName;

    @Column(name = "refund_branch", length = 120)
    private String refundBranch;


    // ========== Getters & Setters ==========

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDate resolvedDate) { this.resolvedDate = resolvedDate; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public LocalDate getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDate refundDate) { this.refundDate = refundDate; }
    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getRefundAccountNumber() { return refundAccountNumber; }
    public void setRefundAccountNumber(String refundAccountNumber) { this.refundAccountNumber = refundAccountNumber; }
    public String getRefundAccountName() { return refundAccountName; }
    public void setRefundAccountName(String refundAccountName) { this.refundAccountName = refundAccountName; }
    public String getRefundBankName() { return refundBankName; }
    public void setRefundBankName(String refundBankName) { this.refundBankName = refundBankName; }
    public String getRefundBranch() { return refundBranch; }
    public void setRefundBranch(String refundBranch) { this.refundBranch = refundBranch; }
}
