package com.mgt.service;

import com.mgt.dao.SellerDAO;
import com.mgt.dao.SellerWithdrawDAO;
import com.mgt.model.Seller;
import com.mgt.model.SellerWithdraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SellerWithdrawService {

    @Autowired
    private SellerWithdrawDAO withdrawDAO;

    @Autowired
    private SellerDAO sellerDAO;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private SellerWalletService walletService;

    public SellerWithdraw request(long sellerId, SellerWithdraw request) {
        Seller seller = sellerDAO.getById(sellerId);
        if (seller == null) throw new RuntimeException("Seller not found: " + sellerId);
        if (request.getAmount() == null || request.getAmount() < 500) {
            throw new RuntimeException("Minimum withdrawal amount is 500");
        }

        double availableBalance = getAvailableBalance(sellerId);
        if (request.getAmount() > availableBalance) {
            throw new RuntimeException("Withdraw amount exceeds available balance. Available: " + availableBalance);
        }

        request.setId(0);
        request.setSeller(seller);
        request.setStatus("PENDING");
        request.setRequestDate(LocalDate.now());
        withdrawDAO.save(request);
        return request;
    }

    public List<SellerWithdraw> getAll() { return withdrawDAO.getAll(); }

    public List<SellerWithdraw> getBySeller(long sellerId) { return withdrawDAO.getBySeller(sellerId); }

    public List<SellerWithdraw> getByStatus(String status) { return withdrawDAO.getByStatus(status); }

    public double getAvailableBalance(long sellerId) {
        double walletAvailable = walletService.get(sellerId).getAvailableBalance() != null
                ? walletService.get(sellerId).getAvailableBalance() : 0.0;
        double locked = withdrawDAO.sumBySellerAndStatuses(sellerId, Arrays.asList("PENDING", "APPROVED", "PROCESSING"));
        return Math.max(0.0, walletAvailable - locked);
    }

    public boolean approve(long id, String txRef, String note) {
        SellerWithdraw withdraw = withdrawDAO.getById(id);
        if (withdraw == null) return false;
        if (!"PENDING".equalsIgnoreCase(withdraw.getStatus()) && !"APPROVED".equalsIgnoreCase(withdraw.getStatus()) && !"PROCESSING".equalsIgnoreCase(withdraw.getStatus())) {
            throw new RuntimeException("Only pending/approved withdrawal can be paid");
        }
        withdraw.setStatus("PAID");
        withdraw.setTransactionRef(txRef);
        withdraw.setAdminNote(note);
        withdraw.setProcessedDate(LocalDate.now());
        withdrawDAO.update(withdraw);
        walletService.debitWithdrawal(withdraw);
        return true;
    }

    public boolean reject(long id, String note) {
        SellerWithdraw withdraw = withdrawDAO.getById(id);
        if (withdraw == null) return false;
        withdraw.setStatus("REJECTED");
        withdraw.setAdminNote(note);
        withdraw.setProcessedDate(LocalDate.now());
        withdrawDAO.update(withdraw);
        return true;
    }

    public boolean delete(long id) {
        if (withdrawDAO.getById(id) == null) return false;
        withdrawDAO.delete(id);
        return true;
    }

    private double toDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }
}
