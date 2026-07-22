package com.mgt.dao;

import com.mgt.model.OrderReturn;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class OrderReturnDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(OrderReturn orderReturn) {
        entityManager.persist(orderReturn);
    }

    public List<OrderReturn> getAll() {
        return entityManager.createQuery(
            "from order_return r order by r.requestDate desc", OrderReturn.class
        ).getResultList();
    }

    public OrderReturn getById(long id) {
        return entityManager.find(OrderReturn.class, id);
    }

    public List<OrderReturn> getByStatus(String status) {
        return entityManager.createQuery(
            "from order_return r where r.status = :status order by r.requestDate desc", OrderReturn.class)
            .setParameter("status", status)
            .getResultList();
    }

    public List<OrderReturn> getByOrderId(long orderId) {
        return entityManager.createQuery(
            "from order_return r where r.order.id = :orderId order by r.requestDate desc", OrderReturn.class)
            .setParameter("orderId", orderId)
            .getResultList();
    }

    public boolean hasActiveReturn(long orderId, String productName) {
        Long count = entityManager.createQuery(
            "select count(r) from order_return r where r.order.id = :orderId " +
            "and lower(trim(r.productName)) = :productName and r.status in ('PENDING', 'APPROVED')",
            Long.class)
            .setParameter("orderId", orderId)
            .setParameter("productName", productName == null ? "" : productName.trim().toLowerCase())
            .getSingleResult();
        return count != null && count > 0;
    }

    public void update(OrderReturn orderReturn) {
        entityManager.merge(orderReturn);
    }

    public void updateStatus(long id, String status, String adminNote) {
        entityManager.createQuery(
            "update order_return r set r.status = :status, r.adminNote = :note, r.resolvedDate = :date where r.id = :id")
            .setParameter("status", status)
            .setParameter("note", adminNote)
            .setParameter("date", LocalDate.now())
            .setParameter("id", id)
            .executeUpdate();
    }

    public void delete(long id) {
        entityManager.createQuery("delete from order_return where id = :id")
            .setParameter("id", id)
            .executeUpdate();
    }
}
