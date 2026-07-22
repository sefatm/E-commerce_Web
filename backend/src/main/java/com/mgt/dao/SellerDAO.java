package com.mgt.dao;

import com.mgt.model.Seller;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class SellerDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Seller seller) {
        em.persist(seller);
    }

    public Seller getById(long id) {
        return em.find(Seller.class, id);
    }

    public List<Seller> getAll() {
        return em.createQuery("from seller order by id desc", Seller.class).getResultList();
    }

    public List<Seller> getByStatus(String status) {
        return em.createQuery(
                "from seller where upper(coalesce(trim(status), 'PENDING')) = :status order by id desc",
                Seller.class)
                .setParameter("status", status.toUpperCase())
                .getResultList();
    }

    public List<Seller> getApproved() {
        return getByStatus("APPROVED");
    }

    public Seller getByUserId(long userId) {
        List<Seller> sellers = em.createQuery(
                "from seller where user.id = :userId order by id desc", Seller.class)
                .setParameter("userId", userId)
                .getResultList();
        return sellers.isEmpty() ? null : sellers.get(0);
    }

    public void update(Seller seller) {
        em.merge(seller);
    }

    public void delete(long id) {
        em.createQuery("delete from seller where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
