package com.mgt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgt.dao.AddProductDAO;
import com.mgt.dao.OfferDAO;
import com.mgt.model.AddProduct;

@Service
public class AddProductService {

	@Autowired
	AddProductDAO addProductDAO;

	@Autowired
	OfferDAO offerDAO;
	
	public void create(AddProduct product) {
		product.setApprovalStatus(normalizeApprovalStatus(product.getApprovalStatus(), "PENDING"));
		addProductDAO.save(product);
	}

	public List<AddProduct> getall() {
		return addProductDAO.getAll();
	}

	public List<AddProduct> getPublicProducts() {
		return addProductDAO.getPublicProducts();
	}

	public List<AddProduct> getPendingProducts() {
		return addProductDAO.getByApprovalStatus("PENDING");
	}

	public List<AddProduct> getRejectedProducts() {
		return addProductDAO.getByApprovalStatus("REJECTED");
	}

	public boolean approve(int id) {
		AddProduct product = addProductDAO.getById(id);
		if (product == null) return false;
		product.setApprovalStatus("APPROVED");
		product.setRejectionReason(null);
		addProductDAO.update(product);
		return true;
	}

	public boolean reject(int id, String reason) {
		AddProduct product = addProductDAO.getById(id);
		if (product == null) return false;
		product.setApprovalStatus("REJECTED");
		product.setRejectionReason(reason);
		addProductDAO.update(product);
		return true;
	}

	public void update(AddProduct product) {
		product.setApprovalStatus(normalizeApprovalStatus(product.getApprovalStatus(), "PENDING"));
		addProductDAO.update(product);
	}

	public void delete(int id) {
		offerDAO.detachProduct(id);

		addProductDAO.delete(id);
	}

	public AddProduct getById(int id) {
        return addProductDAO.getById(id);
    }

	public List<AddProduct> getByCategoryId(long categoryId){
		return addProductDAO.getByCategoryId(categoryId);
	}

	public List<AddProduct> getApprovedByCategoryId(long categoryId){
		return addProductDAO.getApprovedByCategoryId(categoryId);
	}

	public List<AddProduct> getBySellerId(long sellerId){
		return addProductDAO.getBySellerId(sellerId);
	}

	public Long countByCategoryId(long categoryId){
		return addProductDAO.countByCategoryId(categoryId);
	}

	private String normalizeApprovalStatus(String status, String fallback) {
		if (status == null || status.trim().isEmpty()) {
			return fallback;
		}
		String normalized = status.trim().toUpperCase();
		if ("APPROVED".equals(normalized) || "REJECTED".equals(normalized) || "PENDING".equals(normalized)) {
			return normalized;
		}
		return fallback;
	}
	
}
