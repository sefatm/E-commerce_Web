package com.mgt.dao;

import com.mgt.model.SellerWithdraw;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class SellerWithdrawDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(SellerWithdraw withdraw) {
        em.persist(withdraw);
    }

    public SellerWithdraw getById(long id) {
        return em.find(SellerWithdraw.class, id);
    }

    public List<SellerWithdraw> getAll() {
        return em.createQuery("from seller_withdraw order by requestDate desc, id desc", SellerWithdraw.class)
                .getResultList();
    }

    public List<SellerWithdraw> getBySeller(long sellerId) {
        return em.createQuery(
                "from seller_withdraw where seller.id = :sellerId order by requestDate desc, id desc",
                SellerWithdraw.class)
                .setParameter("sellerId", sellerId)
                .getResultList();
    }

    public List<SellerWithdraw> getByStatus(String status) {
        return em.createQuery(
                "from seller_withdraw where status = :status order by requestDate desc, id desc",
                SellerWithdraw.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Double sumBySellerAndStatuses(long sellerId, List<String> statuses) {
        Double total = em.createQuery(
                "select sum(amount) from seller_withdraw where seller.id = :sellerId and status in :statuses",
                Double.class)
                .setParameter("sellerId", sellerId)
                .setParameter("statuses", statuses)
                .getSingleResult();
        return total != null ? total : 0.0;
    }

    public void update(SellerWithdraw withdraw) {
        em.merge(withdraw);
    }

    public void delete(long id) {
        em.createQuery("delete from seller_withdraw where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
