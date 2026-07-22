package com.mgt.service;

import com.mgt.dao.VendorDAO;
import com.mgt.model.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VendorService {

    @Autowired
    VendorDAO vendorDAO;


    public void create(Vendor vendor) {
        vendor.setCreatedAt(LocalDate.now());
        if (vendor.getStatus() == null) vendor.setStatus("pending");
        if (vendor.getCommissionRate() == null) vendor.setCommissionRate(10.0);
        if (vendor.getTotalEarnings() == null) vendor.setTotalEarnings(0.0);
        if (vendor.getTotalOrders() == null)   vendor.setTotalOrders(0);
        if (vendor.getTotalProducts() == null)  vendor.setTotalProducts(0);
        vendorDAO.save(vendor);
    }


    public List<Vendor> getAll() {
        return vendorDAO.getAll();
    }

    public List<Vendor> getByStatus(String status) {
        return vendorDAO.getByStatus(status);
    }

    public Vendor getById(Long id) {
        return vendorDAO.getById(id);
    }

    public List<Vendor> search(String keyword) {
        return vendorDAO.search(keyword);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total",     vendorDAO.totalCount());
        stats.put("active",    vendorDAO.countByStatus("active"));
        stats.put("pending",   vendorDAO.countByStatus("pending"));
        stats.put("suspended", vendorDAO.countByStatus("suspended"));
        return stats;
    }


    public boolean update(Long id, Vendor updated) {
        Vendor existing = vendorDAO.getById(id);
        if (existing == null) return false;

        if (updated.getName() != null)            existing.setName(updated.getName());
        if (updated.getShopName() != null)         existing.setShopName(updated.getShopName());
        if (updated.getShopDescription() != null)  existing.setShopDescription(updated.getShopDescription());
        if (updated.getPhone() != null)            existing.setPhone(updated.getPhone());
        if (updated.getEmail() != null)            existing.setEmail(updated.getEmail());
        if (updated.getAddress() != null)          existing.setAddress(updated.getAddress());
        if (updated.getNidNo() != null)            existing.setNidNo(updated.getNidNo());
        if (updated.getCommissionRate() != null)   existing.setCommissionRate(updated.getCommissionRate());
        if (updated.getBankName() != null)         existing.setBankName(updated.getBankName());
        if (updated.getBankAccountNumber() != null) existing.setBankAccountNumber(updated.getBankAccountNumber());
        if (updated.getBankAccountName() != null)  existing.setBankAccountName(updated.getBankAccountName());
        if (updated.getBankBranch() != null)       existing.setBankBranch(updated.getBankBranch());

        vendorDAO.update(existing);
        return true;
    }

    public boolean approve(Long id) {
        Vendor v = vendorDAO.getById(id);
        if (v == null) return false;
        vendorDAO.updateStatus(id, "active");
        return true;
    }

    public boolean suspend(Long id, String reason) {
        Vendor v = vendorDAO.getById(id);
        if (v == null) return false;
        v.setStatus("suspended");
        v.setRejectionReason(reason);
        vendorDAO.update(v);
        return true;
    }

    public boolean reject(Long id, String reason) {
        Vendor v = vendorDAO.getById(id);
        if (v == null) return false;
        v.setStatus("rejected");
        v.setRejectionReason(reason);
        vendorDAO.update(v);
        return true;
    }

    public boolean updateCommission(Long id, Double rate) {
        if (vendorDAO.getById(id) == null) return false;
        vendorDAO.updateCommission(id, rate);
        return true;
    }


    public boolean delete(Long id) {
        if (vendorDAO.getById(id) == null) return false;
        vendorDAO.delete(id);
        return true;
    }
}
