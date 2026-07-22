package com.mgt.service;

import com.mgt.dao.ProductAttributeDAO;
import com.mgt.model.ProductAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ProductAttributeService {

    @Autowired
    private ProductAttributeDAO attributeDAO;

    public List<ProductAttribute> getAll() { return attributeDAO.getAll(); }

    public ProductAttribute create(Map<String, Object> body) {
        ProductAttribute attr = new ProductAttribute();
        attr.setName(body.get("name").toString().trim());
        attr.setValues(body.getOrDefault("values", "").toString().trim());
        attributeDAO.save(attr);
        return attr;
    }

    public ProductAttribute update(long id, Map<String, Object> body) {
        ProductAttribute attr = attributeDAO.getById(id);
        if (attr == null) throw new RuntimeException("Attribute not found: " + id);
        if (body.containsKey("name"))   attr.setName(body.get("name").toString().trim());
        if (body.containsKey("values")) attr.setValues(body.get("values").toString().trim());
        attributeDAO.update(attr);
        return attr;
    }

    public void delete(long id) { attributeDAO.delete(id); }

    /** Returns the values list as a String array for convenience */
    public List<String> getValuesList(long id) {
        ProductAttribute attr = attributeDAO.getById(id);
        if (attr == null || attr.getValues() == null || attr.getValues().trim().isEmpty())
            return Collections.emptyList();
        return Arrays.asList(attr.getValues().split(","));
    }
}
