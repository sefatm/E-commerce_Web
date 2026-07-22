package com.mgt.dao;

import com.mgt.model.Offer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class OfferDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Offer offer) {
        entityManager.persist(offer);
    }

    public List<Offer> getAll() {
        return entityManager.createQuery("from offer order by createdAt desc", Offer.class)
                .getResultList();
    }

    public Offer getById(long id) {
        return entityManager.find(Offer.class, id);
    }

    // Customer frontend এর জন্য — শুধু active এবং date range এর মধ্যে
    public List<Offer> getActiveOffers() {
        LocalDate today = LocalDate.now();
        return entityManager.createQuery(
                "from offer o where o.status = 'ACTIVE' " +
                "AND (o.startDate IS NULL OR o.startDate <= :today) " +
                "AND (o.endDate IS NULL OR o.endDate >= :today)",
                Offer.class)
                .setParameter("today", today)
                .getResultList();
    }

    public List<Offer> getOffersByProductId(long productId) {
        LocalDate today = LocalDate.now();
        return entityManager.createQuery(
                "from offer o where o.product.id = :pid AND o.status = 'ACTIVE' " +
                "AND (o.endDate IS NULL OR o.endDate >= :today)",
                Offer.class)
                .setParameter("pid", productId)
                .setParameter("today", today)
                .getResultList();
    }

    public List<Offer> getOffersByCategoryId(long categoryId) {
        LocalDate today = LocalDate.now();
        return entityManager.createQuery(
                "from offer o where o.category.id = :cid AND o.status = 'ACTIVE' " +
                "AND (o.endDate IS NULL OR o.endDate >= :today)",
                Offer.class)
                .setParameter("cid", categoryId)
                .setParameter("today", today)
                .getResultList();
    }

    public void update(Offer offer) {
        entityManager.merge(offer);
    }

    // Category delete করার আগে সেই category র reference NULL করো
    public void detachCategory(long categoryId) {
        entityManager.createQuery(
                "update offer o set o.category = null where o.category.id = :cid")
                .setParameter("cid", categoryId)
                .executeUpdate();
    }

    // Product delete করার আগে সেই product এর reference NULL করো
    public void detachProduct(int productId) {
        entityManager.createQuery(
                "update offer o set o.product = null where o.product.id = :pid")
                .setParameter("pid", productId)
                .executeUpdate();
    }

    public void delete(long id) {
        entityManager.createQuery("delete from offer where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
