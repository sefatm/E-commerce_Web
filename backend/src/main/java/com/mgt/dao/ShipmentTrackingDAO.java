package com.mgt.dao;

import com.mgt.model.ShipmentTracking;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ShipmentTrackingDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(ShipmentTracking tracking) {
        em.persist(tracking);
    }

    public List<ShipmentTracking> getAll() {
        return em.createQuery(
                "from ShipmentTracking order by createdAt desc",
                ShipmentTracking.class)
                .getResultList();
    }

    public ShipmentTracking getById(Long id) {
        return em.find(ShipmentTracking.class, id);
    }

    public ShipmentTracking getByOrderId(Long orderId) {
        List<ShipmentTracking> list = em.createQuery(
                "from ShipmentTracking where orderId = :oid order by createdAt desc",
                ShipmentTracking.class)
                .setParameter("oid", orderId)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public ShipmentTracking getByTrackingNumber(String trackingNumber) {
        List<ShipmentTracking> list = em.createQuery(
                "from ShipmentTracking where trackingNumber = :tn",
                ShipmentTracking.class)
                .setParameter("tn", trackingNumber)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<ShipmentTracking> getByStatus(String status) {
        return em.createQuery(
                "from ShipmentTracking where status = :status order by createdAt desc",
                ShipmentTracking.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<ShipmentTracking> search(String keyword) {
        String kw = "%" + keyword.toLowerCase() + "%";
        return em.createQuery(
                "from ShipmentTracking where lower(trackingNumber) like :kw " +
                "or lower(recipientName) like :kw or lower(recipientPhone) like :kw " +
                "or lower(district) like :kw order by createdAt desc",
                ShipmentTracking.class)
                .setParameter("kw", kw)
                .getResultList();
    }

    // Status count for dashboard stats
    public Long countByStatus(String status) {
        return (Long) em.createQuery(
                "select count(t) from ShipmentTracking t where t.status = :status")
                .setParameter("status", status)
                .getSingleResult();
    }

    public Long totalCount() {
        return (Long) em.createQuery("select count(t) from ShipmentTracking t")
                .getSingleResult();
    }

    public void update(ShipmentTracking tracking) {
        em.merge(tracking);
    }

    public void delete(Long id) {
        em.createQuery("delete from ShipmentTracking where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
