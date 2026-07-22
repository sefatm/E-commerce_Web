package com.mgt.dao;

import com.mgt.model.ShippingZone;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ShippingZoneDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(ShippingZone zone) {
        em.persist(zone);
    }

    public List<ShippingZone> getAll() {
        return em.createQuery("from ShippingZone order by id asc", ShippingZone.class)
                .getResultList();
    }

    public List<ShippingZone> getActive() {
        return em.createQuery("from ShippingZone where status = 'active' order by id asc", ShippingZone.class)
                .getResultList();
    }

    public ShippingZone getById(Long id) {
        return em.find(ShippingZone.class, id);
    }

    public void update(ShippingZone zone) {
        em.merge(zone);
    }

    public void delete(Long id) {
        em.createQuery("delete from ShippingZone where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Long count() {
        return (Long) em.createQuery("select count(z) from ShippingZone z")
                .getSingleResult();
    }
}
