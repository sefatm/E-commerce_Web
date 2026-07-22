package com.mgt.service;

import com.mgt.dao.CouponDAO;
import com.mgt.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponService {

    @Autowired
    CouponDAO couponDAO;

    public void create(Coupon coupon) {
        coupon.setCreatedAt(LocalDate.now());
        coupon.setUsedCount(0);
        coupon.setStatus("ACTIVE");
        coupon.setCode(coupon.getCode().toUpperCase().trim());
        couponDAO.save(coupon);
    }

    public List<Coupon> getAll() { return couponDAO.getAll(); }

    public Coupon getById(long id) { return couponDAO.getById(id); }

    public void update(Coupon coupon) {
        if (coupon.getCode() != null) {
            coupon.setCode(coupon.getCode().toUpperCase().trim());
        }
        couponDAO.update(coupon);
    }

    public void delete(long id) { couponDAO.delete(id); }

  
    public Map<String, Object> validateAndApply(String code, Double orderTotal) {
        Map<String, Object> result = new HashMap<>();
        Coupon coupon = couponDAO.findValidCoupon(code.toUpperCase().trim());

        if (coupon == null) {
            result.put("valid", false);
            result.put("message", "Invalid or expired coupon code.");
            return result;
        }

        if (coupon.getMinOrderAmount() != null && orderTotal < coupon.getMinOrderAmount()) {
            result.put("valid", false);
            result.put("message", "Minimum order amount ৳" + coupon.getMinOrderAmount() + " required.");
            return result;
        }

        double discountAmount;
        if ("PERCENTAGE".equals(coupon.getDiscountType())) {
            discountAmount = (orderTotal * coupon.getDiscountValue()) / 100.0;
            if (coupon.getMaxDiscountAmount() != null && discountAmount > coupon.getMaxDiscountAmount()) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        } else { 
            discountAmount = coupon.getDiscountValue();
            if (discountAmount > orderTotal) discountAmount = orderTotal;
        }

        result.put("valid", true);
        result.put("couponId", coupon.getId());
        result.put("discountAmount", discountAmount);
        result.put("finalTotal", orderTotal - discountAmount);
        result.put("message", "Coupon applied! You saved ৳" + String.format("%.2f", discountAmount));
        return result;
    }

    public void markCouponAsUsed(long couponId) {
        couponDAO.incrementUsedCount(couponId);
    }
}
