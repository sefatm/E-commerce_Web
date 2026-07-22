package com.mgt.dao;

import com.mgt.model.Commission;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class CommissionDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Commission commission) {
        em.persist(commission);
    }

    public Commission getById(long id) {
        return em.find(Commission.class, id);
    }

    public List<Commission> getAll() {
        return em.createQuery("from commission order by createdAt desc, id desc", Commission.class)
                .getResultList();
    }

    public List<Commission> getBySellerId(long sellerId) {
        return em.createQuery("from commission where seller.id = :sellerId order by createdAt desc, id desc", Commission.class)
                .setParameter("sellerId", sellerId)
                .getResultList();
    }

    public List<Commission> getByOrderId(long orderId) {
        return em.createQuery("from commission where order.id = :orderId order by createdAt desc, id desc", Commission.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<Commission> getByStatus(String status) {
        return em.createQuery("from commission where status = :status order by createdAt desc, id desc", Commission.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Commission> getBySellerAndStatus(long sellerId, String status) {
        return em.createQuery(
                "from commission where seller.id = :sellerId and status = :status order by createdAt desc, id desc",
                Commission.class)
                .setParameter("sellerId", sellerId)
                .setParameter("status", status)
                .getResultList();
    }

    public Commission getByOrderAndSeller(long orderId, long sellerId) {
        List<Commission> rows = em.createQuery("from commission where order.id=:orderId and seller.id=:sellerId", Commission.class)
                .setParameter("orderId", orderId).setParameter("sellerId", sellerId).setMaxResults(1).getResultList();
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void update(Commission commission) {
        em.merge(commission);
    }
}
