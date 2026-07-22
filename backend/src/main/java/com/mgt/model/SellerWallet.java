package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "seller_wallet")
@Table(name = "seller_wallet", uniqueConstraints = @UniqueConstraint(name = "uk_wallet_seller", columnNames = "seller_id"))
public class SellerWallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"user"})
    private Seller seller;
    @Column(name="pending_balance")
    private Double pendingBalance = 0.0;
    @Column(name="on_hold_balance")
    private Double onHoldBalance = 0.0;
    @Column(name="available_balance")
    private Double availableBalance = 0.0;
    @Column(name="withdrawn_balance")
    private Double withdrawnBalance = 0.0;
    @Column(name="adjustment_balance")
    private Double adjustmentBalance = 0.0;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    public long getId(){return id;} public void setId(long id){this.id=id;}
    public Seller getSeller(){return seller;} public void setSeller(Seller seller){this.seller=seller;}
    public Double getPendingBalance(){return pendingBalance;} public void setPendingBalance(Double v){pendingBalance=v;}
    public Double getOnHoldBalance(){return onHoldBalance;} public void setOnHoldBalance(Double v){onHoldBalance=v;}
    public Double getAvailableBalance(){return availableBalance;} public void setAvailableBalance(Double v){availableBalance=v;}
    public Double getWithdrawnBalance(){return withdrawnBalance;} public void setWithdrawnBalance(Double v){withdrawnBalance=v;}
    public Double getAdjustmentBalance(){return adjustmentBalance;} public void setAdjustmentBalance(Double v){adjustmentBalance=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}
