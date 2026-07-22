package com.mgt.dao;

import com.mgt.model.ProductVariant;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ProductVariantDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(ProductVariant v) { em.persist(v); }

    public ProductVariant getById(long id) { return em.find(ProductVariant.class, id); }

    public void delete(long id) {
        em.createQuery("delete from product_variant where id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public List<ProductVariant> getByProductId(int productId) {
        return em.createQuery(
                "from product_variant where product.id = :pid order by attributeName, attributeValue",
                ProductVariant.class)
                .setParameter("pid", productId).getResultList();
    }

    public void update(ProductVariant v) { em.merge(v); }
}
