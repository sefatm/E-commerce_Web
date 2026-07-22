package com.mgt.dao;

import com.mgt.model.Coupon;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class CouponDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Coupon coupon) {
        entityManager.persist(coupon);
    }

    public List<Coupon> getAll() {
        return entityManager.createQuery("from coupon order by createdAt desc", Coupon.class)
                .getResultList();
    }

    public Coupon getById(long id) {
        return entityManager.find(Coupon.class, id);
    }

    // Checkout এ validate করার জন্য — সব condition একসাথে check
    public Coupon findValidCoupon(String code) {
        LocalDate today = LocalDate.now();
        try {
            return entityManager.createQuery(
                "from coupon c where c.code = :code " +
                "AND c.status = 'ACTIVE' " +
                "AND (c.startDate IS NULL OR c.startDate <= :today) " +
                "AND (c.endDate IS NULL OR c.endDate >= :today) " +
                "AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)",
                Coupon.class)
                .setParameter("code", code)
                .setParameter("today", today)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void update(Coupon coupon) {
        entityManager.merge(coupon);
    }

    public void delete(long id) {
        entityManager.createQuery("delete from coupon where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    // Order place হলে usedCount বাড়ানো
    public void incrementUsedCount(long id) {
        entityManager.createQuery(
                "update coupon c set c.usedCount = c.usedCount + 1 where c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
