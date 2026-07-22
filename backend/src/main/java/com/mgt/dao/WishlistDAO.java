package com.mgt.dao;

import com.mgt.model.Wishlist;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class WishlistDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Wishlist wishlist) {
        em.persist(wishlist);
    }

    public Wishlist getByUserAndProduct(long userId, int productId) {
        List<Wishlist> items = em.createQuery(
                "from wishlist where user.id = :userId and product.id = :productId",
                Wishlist.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .getResultList();
        return items.isEmpty() ? null : items.get(0);
    }

    public List<Wishlist> getByUserId(long userId) {
        return em.createQuery(
                "from wishlist where user.id = :userId order by createdAt desc, id desc",
                Wishlist.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void delete(long id) {
        em.createQuery("delete from wishlist where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
