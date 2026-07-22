package com.mgt.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mgt.model.AddProduct;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository(value = "addProductDAO")
@Transactional
public class AddProductDAO {
	
	@PersistenceContext
    private EntityManager entityManager;

    public void save(AddProduct product) {
    	entityManager.persist(product);
	}
    
    public List<AddProduct> getAll(){
    	String sql = "from product order by id desc";
        List<AddProduct> products = entityManager.createQuery(sql).getResultList();
        return products;
    }

    public List<AddProduct> getByApprovalStatus(String status) {
        return entityManager.createQuery(
                "from product where upper(coalesce(trim(approvalStatus), 'PENDING')) = :status order by id desc",
                AddProduct.class)
            .setParameter("status", status.toUpperCase())
            .getResultList();
    }

    public List<AddProduct> getPublicProducts() {
        return getByApprovalStatus("APPROVED");
    }
    
    public AddProduct getById(int id) {
        return entityManager.find(AddProduct.class, id);
    }

	public void update(AddProduct product) {
		entityManager.merge(product);
	}

	public void delete(int id) {
		entityManager.createQuery("delete from product where id = :id")
		  .setParameter("id", id)
		  .executeUpdate();
		
	}

	public List<AddProduct> getByCategoryId(long categoryId){
		String sql = "from product where category.id = :cid";
		return entityManager.createQuery(sql)
				.setParameter("cid", categoryId)
				.getResultList();
	}

	public List<AddProduct> getApprovedByCategoryId(long categoryId){
		String sql = "from product where category.id = :cid and approvalStatus = :status";
		return entityManager.createQuery(sql, AddProduct.class)
				.setParameter("cid", categoryId)
				.setParameter("status", "APPROVED")
				.getResultList();
	}

	public List<AddProduct> getBySellerId(long sellerId){
		String sql = "from product where seller.id = :sid order by id desc";
		return entityManager.createQuery(sql, AddProduct.class)
				.setParameter("sid", sellerId)
				.getResultList();
	}

	public Long countByCategoryId(long categoryId){
		String sql = "select count(p) from product p where p.category.id = :cid";
		return (Long) entityManager.createQuery(sql)
				.setParameter("cid", categoryId)
				.getSingleResult();
	}
}
