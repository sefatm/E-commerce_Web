package com.mgt.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mgt.model.ProductCategory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository(value = "productCategoryDAO")
@Transactional
public class ProductCategoryDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void save(ProductCategory category) {
		entityManager.persist(category);
	}
	
	public List<ProductCategory> getAll(){
		String sql = "from ProductCategory";
		List<ProductCategory> categories = entityManager.createQuery(sql).getResultList();
		return categories;

	};


	public ProductCategory getById(long id) {
		return entityManager.find(ProductCategory.class, id);
	}

	public void update(ProductCategory category) {
		entityManager.merge(category);
	}

	public void delete(long id) {
		entityManager.createQuery("delete from ProductCategory where id = :id")
				.setParameter("id", id)
				.executeUpdate();
	}

	// Category delete করার আগে সেই category র product গুলোর categoryId NULL করো
	public void detachProducts(long categoryId) {
		entityManager.createQuery(
				"update product p set p.category = null where p.category.id = :cid")
				.setParameter("cid", categoryId)
				.executeUpdate();
	}
}
