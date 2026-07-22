package com.mgt.service;

import com.mgt.dao.VendorDAO;
import com.mgt.dao.VendorPayoutDAO;
import com.mgt.model.Vendor;
import com.mgt.model.VendorPayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendorPayoutService {

    @Autowired
    VendorPayoutDAO payoutDAO;

    @Autowired
    VendorDAO vendorDAO;


    public boolean requestPayout(Long vendorId, Double amount, String method, String note) {
        Vendor vendor = vendorDAO.getById(vendorId);
        if (vendor == null) return false;

        VendorPayout payout = new VendorPayout();
        payout.setVendor(vendor);
        payout.setAmount(amount);
        payout.setPaymentMethod(method != null ? method : "bank_transfer");
        payout.setNote(note);
        payout.setStatus("pending");
        payout.setRequestDate(LocalDate.now());

        payoutDAO.save(payout);
        return true;
    }


    public List<VendorPayout> getAll() {
        return payoutDAO.getAll();
    }

    public List<VendorPayout> getByVendor(Long vendorId) {
        return payoutDAO.getByVendor(vendorId);
    }

    public List<VendorPayout> getByStatus(String status) {
        return payoutDAO.getByStatus(status);
    }

    public VendorPayout getById(Long id) {
        return payoutDAO.getById(id);
    }

    public Double getTotalPaidToVendor(Long vendorId) {
        return payoutDAO.getTotalPaidToVendor(vendorId);
    }

    public Long getPendingCount() {
        return payoutDAO.countPending();
    }


    public boolean approvePayout(Long id, String transactionRef, String note) {
        VendorPayout payout = payoutDAO.getById(id);
        if (payout == null) return false;

        payout.setStatus("paid");
        payout.setTransactionRef(transactionRef);
        payout.setNote(note);
        payout.setProcessedDate(LocalDate.now());

        Vendor vendor = payout.getVendor();
        double currentEarnings = vendor.getTotalEarnings() != null ? vendor.getTotalEarnings() : 0;
        vendor.setTotalEarnings(currentEarnings + payout.getAmount());
        vendorDAO.update(vendor);

        payoutDAO.update(payout);
        return true;
    }

    public boolean rejectPayout(Long id, String note) {
        VendorPayout payout = payoutDAO.getById(id);
        if (payout == null) return false;

        payout.setStatus("rejected");
        payout.setNote(note);
        payout.setProcessedDate(LocalDate.now());
        payoutDAO.update(payout);
        return true;
    }


    public boolean delete(Long id) {
        if (payoutDAO.getById(id) == null) return false;
        payoutDAO.delete(id);
        return true;
    }
}
