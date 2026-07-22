package com.mgt.dao;

import com.mgt.model.Review;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ReviewDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Review review) { em.persist(review); }

    public Review getById(long id) { return em.find(Review.class, id); }

    public void delete(long id) {
        em.createQuery("delete from review where id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public List<Review> getAll() {
        return em.createQuery("from review order by createdAt desc", Review.class)
                .getResultList();
    }

    public List<Review> getByProductId(int productId) {
        return em.createQuery(
                "from review where product.id = :pid order by createdAt desc", Review.class)
                .setParameter("pid", productId).getResultList();
    }

    public List<Review> getByRating(int rating) {
        return em.createQuery(
                "from review where rating = :r order by createdAt desc", Review.class)
                .setParameter("r", rating).getResultList();
    }

    public List<Review> getByStatus(String status) {
        return em.createQuery(
                "from review where status = :s order by createdAt desc", Review.class)
                .setParameter("s", status).getResultList();
    }

    public boolean existsByCustomerAndProduct(long customerId, int productId) {
        Long count = em.createQuery(
                "select count(r) from review r where r.customer.id = :cid and r.product.id = :pid",
                Long.class)
                .setParameter("cid", customerId)
                .setParameter("pid", productId)
                .getSingleResult();
        return count > 0;
    }

    public Double getAverageRatingByProduct(int productId) {
        return em.createQuery(
                "select avg(r.rating) from review r where r.product.id = :pid", Double.class)
                .setParameter("pid", productId)
                .getSingleResult();
    }
}
