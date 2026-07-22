package com.mgt.service;

import com.mgt.dao.AddProductDAO;
import com.mgt.dao.ProductVariantDAO;
import com.mgt.model.AddProduct;
import com.mgt.model.ProductVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductVariantService {

    @Autowired private ProductVariantDAO variantDAO;
    @Autowired private AddProductDAO productDAO;

    public List<ProductVariant> getByProduct(int productId) {
        return variantDAO.getByProductId(productId);
    }

    public ProductVariant create(Map<String, Object> body) {
        int productId = Integer.parseInt(body.get("productId").toString());
        AddProduct product = productDAO.getById(productId);
        if (product == null) throw new RuntimeException("Product not found: " + productId);

        ProductVariant v = new ProductVariant();
        v.setProduct(product);
        v.setAttributeName(body.getOrDefault("attributeName", "").toString().trim());
        v.setAttributeValue(body.getOrDefault("attributeValue", "").toString().trim());
        v.setSku(body.getOrDefault("sku", "").toString().trim());
        if (body.containsKey("priceAdjustment"))
            v.setPriceAdjustment(Double.parseDouble(body.get("priceAdjustment").toString()));
        if (body.containsKey("stock"))
            v.setStock(Integer.parseInt(body.get("stock").toString()));

        variantDAO.save(v);
        return v;
    }

    public ProductVariant update(long id, Map<String, Object> body) {
        ProductVariant v = variantDAO.getById(id);
        if (v == null) throw new RuntimeException("Variant not found: " + id);
        if (body.containsKey("attributeName"))   v.setAttributeName(body.get("attributeName").toString());
        if (body.containsKey("attributeValue"))  v.setAttributeValue(body.get("attributeValue").toString());
        if (body.containsKey("priceAdjustment")) v.setPriceAdjustment(Double.parseDouble(body.get("priceAdjustment").toString()));
        if (body.containsKey("stock"))           v.setStock(Integer.parseInt(body.get("stock").toString()));
        if (body.containsKey("sku"))             v.setSku(body.get("sku").toString());
        variantDAO.update(v);
        return v;
    }

    public void delete(long id) { variantDAO.delete(id); }
}
