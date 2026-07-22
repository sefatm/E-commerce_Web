package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "wallet_transaction")
@Table(name = "wallet_transaction", indexes = {@Index(name="idx_wallet_tx_seller", columnList="seller_id"), @Index(name="idx_wallet_tx_reference", columnList="reference_type,reference_id")})
public class WalletTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
    @ManyToOne @JoinColumn(name="seller_id", nullable=false) @JsonIgnoreProperties({"user"}) private Seller seller;
    @Column(length=30, nullable=false) private String type;
    @Column(name="balance_bucket", length=30, nullable=false) private String balanceBucket;
    private Double amount;
    @Column(name="balance_after", nullable=false) private Double balanceAfter;
    @Column(name="reference_type", length=40) private String referenceType;
    @Column(name="reference_id") private Long referenceId;
    @Column(name="transaction_ref", length=120) private String transactionRef;
    @Column(length=500) private String note;
    @Column(name="created_at") private LocalDateTime createdAt;
    public long getId(){return id;} public void setId(long id){this.id=id;}
    public Seller getSeller(){return seller;} public void setSeller(Seller v){seller=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getBalanceBucket(){return balanceBucket;} public void setBalanceBucket(String v){balanceBucket=v;}
    public Double getAmount(){return amount;} public void setAmount(Double v){amount=v;}
    public Double getBalanceAfter(){return balanceAfter;} public void setBalanceAfter(Double v){balanceAfter=v;}
    public String getReferenceType(){return referenceType;} public void setReferenceType(String v){referenceType=v;}
    public Long getReferenceId(){return referenceId;} public void setReferenceId(Long v){referenceId=v;}
    public String getTransactionRef(){return transactionRef;} public void setTransactionRef(String v){transactionRef=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
