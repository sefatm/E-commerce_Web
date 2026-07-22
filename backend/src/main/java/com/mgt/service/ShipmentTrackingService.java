package com.mgt.service;

import com.mgt.dao.DeliveryMethodDAO;
import com.mgt.dao.ShipmentTrackingDAO;
import com.mgt.model.DeliveryMethod;
import com.mgt.model.ShipmentTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ShipmentTrackingService {

    @Autowired
    ShipmentTrackingDAO trackingDAO;

    @Autowired
    DeliveryMethodDAO methodDAO;


    public ShipmentTracking create(ShipmentTracking tracking, Long methodId) {
        if (methodId != null) {
            DeliveryMethod method = methodDAO.getById(methodId);
            if (method != null) {
                tracking.setMethod(method);
                if (tracking.getCarrier() == null) {
                    tracking.setCarrier(method.getCarrier());
                }
                if (tracking.getShippingCharge() == null) {
                    tracking.setShippingCharge(method.getCharge());
                }
            }
        }

        if (tracking.getTrackingNumber() == null || tracking.getTrackingNumber().trim().isEmpty()) {
            tracking.setTrackingNumber(generateTrackingNumber());
        }

        tracking.setStatus("pending");
        tracking.setCreatedAt(LocalDateTime.now());
        tracking.setUpdatedAt(LocalDateTime.now());

        trackingDAO.save(tracking);
        return tracking;
    }


    public List<ShipmentTracking> getAll() {
        return trackingDAO.getAll();
    }

    public ShipmentTracking getById(Long id) {
        return trackingDAO.getById(id);
    }

    public ShipmentTracking getByOrderId(Long orderId) {
        return trackingDAO.getByOrderId(orderId);
    }

    public ShipmentTracking getByTrackingNumber(String trackingNumber) {
        return trackingDAO.getByTrackingNumber(trackingNumber);
    }

    public List<ShipmentTracking> getByStatus(String status) {
        return trackingDAO.getByStatus(status);
    }

    public List<ShipmentTracking> search(String keyword) {
        return trackingDAO.search(keyword);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total",          trackingDAO.totalCount());
        stats.put("pending",        trackingDAO.countByStatus("pending"));
        stats.put("pickedUp",       trackingDAO.countByStatus("picked_up"));
        stats.put("inTransit",      trackingDAO.countByStatus("in_transit"));
        stats.put("outForDelivery", trackingDAO.countByStatus("out_for_delivery"));
        stats.put("delivered",      trackingDAO.countByStatus("delivered"));
        stats.put("failed",         trackingDAO.countByStatus("failed"));
        stats.put("returned",       trackingDAO.countByStatus("returned"));
        return stats;
    }


   
    public boolean updateStatus(Long id, String newStatus) {
        ShipmentTracking tracking = trackingDAO.getById(id);
        if (tracking == null) return false;

        tracking.setStatus(newStatus);
        tracking.setUpdatedAt(LocalDateTime.now());

        if ("delivered".equals(newStatus)) {
            tracking.setDeliveredAt(LocalDate.now());
        }

        trackingDAO.update(tracking);
        return true;
    }

    public boolean update(Long id, ShipmentTracking updated) {
        ShipmentTracking existing = trackingDAO.getById(id);
        if (existing == null) return false;

        if (updated.getTrackingNumber() != null)  existing.setTrackingNumber(updated.getTrackingNumber());
        if (updated.getCarrier() != null)         existing.setCarrier(updated.getCarrier());
        if (updated.getStatus() != null)          existing.setStatus(updated.getStatus());
        if (updated.getRecipientName() != null)   existing.setRecipientName(updated.getRecipientName());
        if (updated.getRecipientPhone() != null)  existing.setRecipientPhone(updated.getRecipientPhone());
        if (updated.getShippingAddress() != null) existing.setShippingAddress(updated.getShippingAddress());
        if (updated.getCity() != null)            existing.setCity(updated.getCity());
        if (updated.getDistrict() != null)        existing.setDistrict(updated.getDistrict());
        if (updated.getEstimatedDelivery() != null) existing.setEstimatedDelivery(updated.getEstimatedDelivery());
        if (updated.getNotes() != null)           existing.setNotes(updated.getNotes());
        if (updated.getShippingCharge() != null)  existing.setShippingCharge(updated.getShippingCharge());

        if ("delivered".equals(updated.getStatus()) && existing.getDeliveredAt() == null) {
            existing.setDeliveredAt(LocalDate.now());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        trackingDAO.update(existing);
        return true;
    }


    public boolean delete(Long id) {
        if (trackingDAO.getById(id) == null) return false;
        trackingDAO.delete(id);
        return true;
    }


    private String generateTrackingNumber() {
        String date = LocalDate.now().toString().replace("-", "");
        int rand = new Random().nextInt(90000) + 10000;
        return "RURAL-" + date + "-" + rand;
    }
}
