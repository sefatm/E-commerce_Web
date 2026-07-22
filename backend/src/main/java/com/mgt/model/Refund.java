package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "refund")
@Table(name = "refund")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "return_id")
    @JsonIgnoreProperties({"order"})
    private OrderReturn orderReturn;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({"items"})
    private Order order;

    @Column(name = "refund_amount")
    private Double refundAmount = 0.0;

    @Column(name = "refund_method")
    private String refundMethod;

    @Column(name = "status")
    private String status = "PROCESSED";

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @Column(name = "admin_note", length = 500)
    private String adminNote;

    @Column(name = "refund_account_number", length = 80)
    private String refundAccountNumber;

    @Column(name = "refund_account_name", length = 120)
    private String refundAccountName;

    @Column(name = "refund_bank_name", length = 120)
    private String refundBankName;

    @Column(name = "refund_branch", length = 120)
    private String refundBranch;

    @Column(name = "transaction_ref", length = 120)
    private String transactionRef;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public OrderReturn getOrderReturn() { return orderReturn; }
    public void setOrderReturn(OrderReturn orderReturn) { this.orderReturn = orderReturn; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDate processedDate) { this.processedDate = processedDate; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public String getRefundAccountNumber() { return refundAccountNumber; }
    public void setRefundAccountNumber(String refundAccountNumber) { this.refundAccountNumber = refundAccountNumber; }
    public String getRefundAccountName() { return refundAccountName; }
    public void setRefundAccountName(String refundAccountName) { this.refundAccountName = refundAccountName; }
    public String getRefundBankName() { return refundBankName; }
    public void setRefundBankName(String refundBankName) { this.refundBankName = refundBankName; }
    public String getRefundBranch() { return refundBranch; }
    public void setRefundBranch(String refundBranch) { this.refundBranch = refundBranch; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}
