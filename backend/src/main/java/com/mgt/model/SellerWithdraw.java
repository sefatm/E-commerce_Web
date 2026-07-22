package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "seller_withdraw")
@Table(name = "seller_withdraw")
public class SellerWithdraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"user"})
    private Seller seller;

    private Double amount;
    private String paymentMethod = "bkash";
    private String accountName;
    private String accountNumber;
    private String transactionRef;
    private String status = "PENDING";

    @Column(length = 500)
    private String note;

    @Column(length = 500)
    private String adminNote;

    private LocalDate requestDate;
    private LocalDate processedDate;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDate processedDate) { this.processedDate = processedDate; }
}
