package com.mgt.service;

import com.mgt.dao.ShippingZoneDAO;
import com.mgt.model.ShippingZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShippingZoneService {

    @Autowired
    ShippingZoneDAO zoneDAO;

    public void create(ShippingZone zone) {
        zone.setCreatedAt(LocalDate.now());
        if (zone.getStatus() == null) zone.setStatus("active");
        zoneDAO.save(zone);
    }

    public List<ShippingZone> getAll() {
        return zoneDAO.getAll();
    }

    public List<ShippingZone> getActive() {
        return zoneDAO.getActive();
    }

    public ShippingZone getById(Long id) {
        return zoneDAO.getById(id);
    }

    public boolean update(Long id, ShippingZone updated) {
        ShippingZone existing = zoneDAO.getById(id);
        if (existing == null) return false;

        if (updated.getName() != null)        existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getRegions() != null)     existing.setRegions(updated.getRegions());
        if (updated.getStatus() != null)      existing.setStatus(updated.getStatus());

        zoneDAO.update(existing);
        return true;
    }

    public boolean delete(Long id) {
        if (zoneDAO.getById(id) == null) return false;
        zoneDAO.delete(id);
        return true;
    }

    public Long count() {
        return zoneDAO.count();
    }
}
