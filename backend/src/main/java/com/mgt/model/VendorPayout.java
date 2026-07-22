package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

// ✅ BUG FIX #2 — vendor_payout Dual Foreign Key Bug
// ─────────────────────────────────────────────────────────
// আগের সমস্যা (rural.sql এ):
//   CONSTRAINT FK1 FOREIGN KEY (vendor_id) REFERENCES vendor(id)
//   CONSTRAINT FK2 FOREIGN KEY (vendor_id) REFERENCES seller(id)  ← WRONG!
//
// একটি column (vendor_id) দুটো আলাদা table কে reference করতে পারে না।
// MySQL এ এটা FK constraint error অথবা silent failure ঘটায়।
// seller এবং vendor আলাদা entity — vendor_payout শুধু vendor এর জন্য।
//
// Fix: @JoinColumn শুধু vendor table কে point করবে
// ─────────────────────────────────────────────────────────
@Entity
@Table(name = "vendor_payout")
public class VendorPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FIXED: vendor table এর সাথে একটিমাত্র FK
    // আগে SQL এ seller(id) তেও FK ছিল — সেটা সরানো হয়েছে
    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false)
    private Double amount;

    // bank_transfer | bkash | nagad | cash
    private String paymentMethod = "bank_transfer";

    private String transactionRef;

    // pending | paid | rejected
    private String status = "pending";

    private String note;

    private LocalDate requestDate;
    private LocalDate processedDate;

    // ==================== Getters & Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDate processedDate) { this.processedDate = processedDate; }
}
