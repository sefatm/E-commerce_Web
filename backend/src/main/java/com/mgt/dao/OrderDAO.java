package com.mgt.dao;

import com.mgt.model.Order;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class OrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Order order) {
        entityManager.persist(order);
    }

    public List<Order> getAll() {
        return entityManager.createQuery(
            "from orders o order by o.orderDate desc", Order.class
        ).getResultList();
    }

    public Order getById(long id) {
        return entityManager.find(Order.class, id);
    }

    public Order getByOrderCode(String orderCode) {
        try {
            return entityManager.createQuery(
                "from orders o where lower(trim(o.orderCode)) = :code", Order.class)
                .setParameter("code", orderCode == null ? "" : orderCode.trim().toLowerCase())
                .getSingleResult();
        } catch (Exception e) { return null; }
    }

    // Status filter
    public List<Order> getByStatus(String status) {
        return entityManager.createQuery(
            "from orders o where o.status = :status order by o.orderDate desc", Order.class)
            .setParameter("status", status)
            .getResultList();
    }

    public List<Order> getBySellerId(long sellerId) {
        return entityManager.createQuery(
            "select distinct o from orders o join o.items i, product p " +
            "where i.productId = p.id and p.seller.id = :sellerId " +
            "order by o.orderDate desc", Order.class)
            .setParameter("sellerId", sellerId)
            .getResultList();
    }

    // Date range filter
    public List<Order> getByDateRange(LocalDate from, LocalDate to) {
        return entityManager.createQuery(
            "from orders o where o.orderDate between :from and :to order by o.orderDate desc", Order.class)
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();
    }

    // Search by customer name or phone
    public List<Order> search(String keyword) {
        String like = "%" + keyword.toLowerCase() + "%";
        return entityManager.createQuery(
            "from orders o where lower(o.customerName) like :kw or o.customerPhone like :kw or o.orderCode like :kw order by o.orderDate desc",
            Order.class)
            .setParameter("kw", like)
            .getResultList();
    }

    public void update(Order order) {
        entityManager.merge(order);
    }

    // Status পরিবর্তন করা
    public void updateStatus(long id, String status) {
        entityManager.createQuery(
            "update orders o set o.status = :status where o.id = :id")
            .setParameter("status", status)
            .setParameter("id", id)
            .executeUpdate();
    }

    public void updatePaymentStatus(long id, String paymentStatus) {
        entityManager.createQuery(
            "update orders o set o.paymentStatus = :paymentStatus where o.id = :id")
            .setParameter("paymentStatus", paymentStatus)
            .setParameter("id", id)
            .executeUpdate();
    }

    public void delete(long id) {
        entityManager.createNativeQuery("delete from refund where order_id = :id")
            .setParameter("id", id)
            .executeUpdate();

        entityManager.createNativeQuery(
                "delete rf from refund rf inner join order_return rt on rf.return_id = rt.id where rt.order_id = :id")
            .setParameter("id", id)
            .executeUpdate();

        entityManager.createNativeQuery("delete from order_return where order_id = :id")
            .setParameter("id", id)
            .executeUpdate();

        entityManager.createQuery("delete from commission where order.id = :id")
            .setParameter("id", id)
            .executeUpdate();

        entityManager.createQuery("delete from ShipmentTracking where orderId = :id")
            .setParameter("id", id)
            .executeUpdate();

        entityManager.createQuery("delete from order_item where order.id = :id")
            .setParameter("id", id)
            .executeUpdate();

        entityManager.createQuery("delete from orders where id = :id")
            .setParameter("id", id)
            .executeUpdate();
    }

    // ===== Analytics queries =====

    public Long countTotal() {
        return (Long) entityManager.createQuery("select count(o) from orders o").getSingleResult();
    }

    public Long countByStatus(String status) {
        return (Long) entityManager.createQuery(
            "select count(o) from orders o where o.status = :status")
            .setParameter("status", status)
            .getSingleResult();
    }

    public Double totalRevenue() {
        Object result = entityManager.createQuery(
            "select sum(o.totalAmount) from orders o where o.status != 'CANCELLED'").getSingleResult();
        return result != null ? (Double) result : 0.0;
    }

    public Double revenueThisMonth() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        Object result = entityManager.createQuery(
            "select sum(o.totalAmount) from orders o where o.orderDate >= :start and o.status != 'CANCELLED'")
            .setParameter("start", start)
            .getSingleResult();
        return result != null ? (Double) result : 0.0;
    }

    // Monthly revenue last 6 months (for chart)
    public List<Object[]> monthlyRevenue() {
        return entityManager.createQuery(
            "select month(o.orderDate), sum(o.totalAmount) from orders o " +
            "where o.orderDate >= :since and o.status != 'CANCELLED' " +
            "group by month(o.orderDate) order by month(o.orderDate)",
            Object[].class)
            .setParameter("since", LocalDate.now().minusMonths(6))
            .getResultList();
    }
}
