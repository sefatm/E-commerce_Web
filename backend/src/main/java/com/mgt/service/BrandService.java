package com.mgt.service;

import com.mgt.dao.BrandDAO;
import com.mgt.model.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BrandService {

    @Autowired BrandDAO brandDAO;

    public void create(Brand brand) {
        brand.setCreatedAt(LocalDate.now());
        if (brand.getStatus() == null) brand.setStatus("active");
        if (brand.getProductCount() == null) brand.setProductCount(0);
        brandDAO.save(brand);
    }

    public List<Brand> getAll() { return brandDAO.getAll(); }

    public Brand getById(Long id) { return brandDAO.getById(id); }

    public List<Brand> search(String keyword) { return brandDAO.search(keyword); }

    public boolean update(Long id, Brand updated) {
        Brand existing = brandDAO.getById(id);
        if (existing == null) return false;
        if (updated.getName() != null)        existing.setName(updated.getName());
        if (updated.getLogo() != null)        existing.setLogo(updated.getLogo());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getStatus() != null)      existing.setStatus(updated.getStatus());
        brandDAO.update(existing);
        return true;
    }

    public boolean delete(Long id) {
        if (brandDAO.getById(id) == null) return false;
        brandDAO.delete(id);
        return true;
    }
}
