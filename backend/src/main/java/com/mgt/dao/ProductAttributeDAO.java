package com.mgt.dao;

import com.mgt.model.ProductAttribute;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ProductAttributeDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(ProductAttribute attr) { em.persist(attr); }

    public ProductAttribute getById(long id) { return em.find(ProductAttribute.class, id); }

    public void delete(long id) {
        em.createQuery("delete from product_attribute where id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public List<ProductAttribute> getAll() {
        return em.createQuery("from product_attribute order by name", ProductAttribute.class)
                .getResultList();
    }

    public void update(ProductAttribute attr) { em.merge(attr); }
}
