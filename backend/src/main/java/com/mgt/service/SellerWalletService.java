package com.mgt.service;
import com.mgt.dao.*;
import com.mgt.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class SellerWalletService {
 @Autowired private SellerWalletDAO walletDAO; @Autowired private WalletTransactionDAO txDAO; @Autowired private SellerDAO sellerDAO;
 public SellerWallet get(long sellerId){SellerWallet w=walletDAO.getBySeller(sellerId); if(w==null){Seller s=sellerDAO.getById(sellerId); if(s==null)throw new RuntimeException("Seller not found"); w=new SellerWallet();w.setSeller(s);w.setUpdatedAt(LocalDateTime.now());walletDAO.save(w);} return w;}
 public List<WalletTransaction> transactions(long sellerId){return txDAO.bySeller(sellerId);}
 @Transactional public void creditAvailable(Commission c){ if(c==null||Boolean.TRUE.equals(c.getCreditedToWallet()))return; if(txDAO.exists("COMMISSION",c.getId(),"COMMISSION_CREDIT"))return; SellerWallet w=get(c.getSeller().getId()); double a=v(c.getSellerAmount()); w.setAvailableBalance(v(w.getAvailableBalance())+a);w.setUpdatedAt(LocalDateTime.now());walletDAO.save(w); add(c.getSeller(),"COMMISSION_CREDIT","AVAILABLE",a,w.getAvailableBalance(),"COMMISSION",c.getId(),null,"Commission released for order "+c.getOrder().getOrderCode());}
 @Transactional public void debitWithdrawal(SellerWithdraw wd){ if(txDAO.exists("WITHDRAWAL",wd.getId(),"WITHDRAWAL_DEBIT"))return; SellerWallet w=get(wd.getSeller().getId()); double a=v(wd.getAmount()); if(v(w.getAvailableBalance())<a)throw new RuntimeException("Insufficient wallet balance"); w.setAvailableBalance(v(w.getAvailableBalance())-a);w.setWithdrawnBalance(v(w.getWithdrawnBalance())+a);w.setUpdatedAt(LocalDateTime.now());walletDAO.save(w);add(wd.getSeller(),"WITHDRAWAL_DEBIT","AVAILABLE",-a,w.getAvailableBalance(),"WITHDRAWAL",wd.getId(),wd.getTransactionRef(),"Seller payout completed");}
 @Transactional public void reverseCommission(Commission c,String reason){ if(txDAO.exists("COMMISSION",c.getId(),"REFUND_REVERSAL"))return; SellerWallet w=get(c.getSeller().getId()); double a=v(c.getSellerAmount()); double available=v(w.getAvailableBalance()); double deduct=Math.min(available,a); w.setAvailableBalance(available-deduct); double remain=a-deduct; if(remain>0)w.setAdjustmentBalance(v(w.getAdjustmentBalance())-remain);w.setUpdatedAt(LocalDateTime.now());walletDAO.save(w);add(c.getSeller(),"REFUND_REVERSAL","AVAILABLE",-a,w.getAvailableBalance(),"COMMISSION",c.getId(),null,reason);}
 private void add(Seller s,String type,String bucket,double amount,double after,String rt,long ri,String tr,String note){WalletTransaction t=new WalletTransaction();t.setSeller(s);t.setType(type);t.setBalanceBucket(bucket);t.setAmount(amount);t.setBalanceAfter(after);t.setReferenceType(rt);t.setReferenceId(ri);t.setTransactionRef(tr);t.setNote(note);t.setCreatedAt(LocalDateTime.now());txDAO.save(t);}
 private double v(Double x){return x==null?0:x;}
}
