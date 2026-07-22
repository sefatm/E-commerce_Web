package com.mgt.dao;

import com.mgt.model.VendorPayout;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class VendorPayoutDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(VendorPayout payout) {
        em.persist(payout);
    }

    public List<VendorPayout> getAll() {
        return em.createQuery(
                "from VendorPayout order by requestDate desc",
                VendorPayout.class)
                .getResultList();
    }

    public List<VendorPayout> getByVendor(Long vendorId) {
        return em.createQuery(
                "from VendorPayout where vendor.id = :vid order by requestDate desc",
                VendorPayout.class)
                .setParameter("vid", vendorId)
                .getResultList();
    }

    public List<VendorPayout> getByStatus(String status) {
        return em.createQuery(
                "from VendorPayout where status = :status order by requestDate desc",
                VendorPayout.class)
                .setParameter("status", status)
                .getResultList();
    }

    public VendorPayout getById(Long id) {
        return em.find(VendorPayout.class, id);
    }

    public Double getTotalPaidToVendor(Long vendorId) {
        Object result = em.createQuery(
                "select sum(p.amount) from VendorPayout p where p.vendor.id = :vid and p.status = 'paid'")
                .setParameter("vid", vendorId)
                .getSingleResult();
        return result != null ? (Double) result : 0.0;
    }

    public Long countPending() {
        return (Long) em.createQuery(
                "select count(p) from VendorPayout p where p.status = 'pending'")
                .getSingleResult();
    }

    public void update(VendorPayout payout) {
        em.merge(payout);
    }

    public void delete(Long id) {
        em.createQuery("delete from VendorPayout where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
