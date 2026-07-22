package com.mgt.service;

import com.mgt.dao.DeliveryMethodDAO;
import com.mgt.dao.ShippingZoneDAO;
import com.mgt.model.DeliveryMethod;
import com.mgt.model.ShippingZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryMethodService {

    @Autowired
    DeliveryMethodDAO methodDAO;

    @Autowired
    ShippingZoneDAO zoneDAO;

    public boolean create(DeliveryMethod method, Long zoneId) {
        ShippingZone zone = zoneDAO.getById(zoneId);
        if (zone == null) 
        	return false;

        method.setZone(zone);
        method.setCreatedAt(LocalDate.now());
        
        if (method.getStatus() == null) 
        	method.setStatus("active");
        if (method.getType() == null) 
        	method.setType("flat_rate");
        if (method.getCharge() == null) 
        	method.setCharge(0.0);

        methodDAO.save(method);
        return true;
    }

    public List<DeliveryMethod> getAll() {
        return methodDAO.getAll();
    }

    public List<DeliveryMethod> getByZone(Long zoneId) {
        return methodDAO.getByZone(zoneId);
    }

    public List<DeliveryMethod> getActive() {
        return methodDAO.getActive();
    }

    public DeliveryMethod getById(Long id) {
        return methodDAO.getById(id);
    }

    public List<DeliveryMethod> getAvailableForDistrict(String district) {
        List<ShippingZone> zones = zoneDAO.getActive();

        for (ShippingZone zone : zones) {
            if (zone.getRegions() != null &&
                zone.getRegions().toLowerCase().contains(district.toLowerCase())) {
                return methodDAO.getByZoneAndStatus(zone.getId(), "active");
            }
        }

        for (ShippingZone zone : zones) {
            if (zone.getName() != null &&
                (zone.getName().toLowerCase().contains("nationwide") ||
                 zone.getName().toLowerCase().contains("nationwide") ||
                 zone.getName().toLowerCase().contains("default"))) {
                return methodDAO.getByZoneAndStatus(zone.getId(), "active");
            }
        }

        return methodDAO.getActive(); 
    }

    public boolean update(Long id, DeliveryMethod updated) {
        DeliveryMethod existing = methodDAO.getById(id);
        if (existing == null) {
        	return false;
        }

        if (updated.getName() != null) {
        	existing.setName(updated.getName());
        }
        if (updated.getCarrier() != null){
        	existing.setCarrier(updated.getCarrier());
        }
        if (updated.getDescription() != null) {
        	existing.setDescription(updated.getDescription());
        }
        if (updated.getCharge() != null) {
        	existing.setCharge(updated.getCharge());
        }
        if (updated.getFreeShippingAbove() != null) {
        	existing.setFreeShippingAbove(updated.getFreeShippingAbove());
        }
        if (updated.getEstimatedDays() != null) {
        	existing.setEstimatedDays(updated.getEstimatedDays());
        }
        if (updated.getType() != null) {
        	existing.setType(updated.getType());
        }
        if (updated.getStatus() != null) {
        	existing.setStatus(updated.getStatus());
        }

        if (updated.getZone() != null && updated.getZone().getId() != null) {
        	
            ShippingZone zone = zoneDAO.getById(updated.getZone().getId());
            if (zone != null) existing.setZone(zone);
        }

        methodDAO.update(existing);
        return true;
    }

    public boolean delete(Long id) {
        if (methodDAO.getById(id) == null) return false;
        methodDAO.delete(id);
        return true;
    }
}
