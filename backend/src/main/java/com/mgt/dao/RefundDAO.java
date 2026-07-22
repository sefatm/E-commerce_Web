package com.mgt.dao;

import com.mgt.model.Refund;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class RefundDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Refund refund) {
        em.persist(refund);
    }

    public void update(Refund refund) {
        em.merge(refund);
    }

    public Refund getByReturnId(long returnId) {
        List<Refund> refunds = em.createQuery(
                "from refund where orderReturn.id = :returnId", Refund.class)
                .setParameter("returnId", returnId)
                .getResultList();
        return refunds.isEmpty() ? null : refunds.get(0);
    }

    public List<Refund> getAll() {
        return em.createQuery("from refund order by processedDate desc, id desc", Refund.class)
                .getResultList();
    }
}
