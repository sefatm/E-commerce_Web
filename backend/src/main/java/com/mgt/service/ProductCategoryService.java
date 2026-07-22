package com.mgt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgt.dao.ProductCategoryDAO;
import com.mgt.dao.OfferDAO;
import com.mgt.model.ProductCategory;

@Service(value = "productCategoryService")
public class ProductCategoryService {

	@Autowired
	ProductCategoryDAO categoryDAO;

	@Autowired
	OfferDAO offerDAO;

	public void createCategory(ProductCategory category) {
		categoryDAO.save(category);
	}
	
	public List<ProductCategory> getAll(){
		return categoryDAO.getAll();
	}

	public ProductCategory getById(long id) {
		return categoryDAO.getById(id);
	}

	public void update(ProductCategory category) {
		categoryDAO.update(category);
	}

	public void delete(long id) {
		offerDAO.detachCategory(id);

		categoryDAO.detachProducts(id);

		categoryDAO.delete(id);
	}
}
