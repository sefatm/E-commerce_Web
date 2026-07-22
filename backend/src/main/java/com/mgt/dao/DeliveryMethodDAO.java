package com.mgt.dao;

import com.mgt.model.DeliveryMethod;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class DeliveryMethodDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(DeliveryMethod method) {
        em.persist(method);
    }

    public List<DeliveryMethod> getAll() {
        return em.createQuery("from DeliveryMethod order by id asc", DeliveryMethod.class)
                .getResultList();
    }

    public List<DeliveryMethod> getByZone(Long zoneId) {
        return em.createQuery(
                "from DeliveryMethod where zone.id = :zid order by charge asc",
                DeliveryMethod.class)
                .setParameter("zid", zoneId)
                .getResultList();
    }

    public List<DeliveryMethod> getActive() {
        return em.createQuery(
                "from DeliveryMethod where status = 'active' order by charge asc",
                DeliveryMethod.class)
                .getResultList();
    }

    public List<DeliveryMethod> getByZoneAndStatus(Long zoneId, String status) {
        return em.createQuery(
                "from DeliveryMethod where zone.id = :zid and status = :status",
                DeliveryMethod.class)
                .setParameter("zid", zoneId)
                .setParameter("status", status)
                .getResultList();
    }

    public DeliveryMethod getById(Long id) {
        return em.find(DeliveryMethod.class, id);
    }

    public void update(DeliveryMethod method) {
        em.merge(method);
    }

    public void delete(Long id) {
        em.createQuery("delete from DeliveryMethod where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Long countByZone(Long zoneId) {
        return (Long) em.createQuery(
                "select count(m) from DeliveryMethod m where m.zone.id = :zid")
                .setParameter("zid", zoneId)
                .getSingleResult();
    }
}
