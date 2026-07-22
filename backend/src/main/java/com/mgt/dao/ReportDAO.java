package com.mgt.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class ReportDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ===== SALES REPORT =====

    // Date range এর মধ্যে সব orders
    public List<Object[]> getSalesData(LocalDate from, LocalDate to) {
        return entityManager.createQuery(
            "select o.orderCode, o.customerName, o.orderDate, " +
            "o.totalAmount, o.paymentMethod, o.status " +
            "from orders o " +
            "where o.orderDate between :from and :to " +
            "order by o.orderDate desc",
            Object[].class)
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();
    }

    public Double getTotalRevenueBetween(LocalDate from, LocalDate to) {
        Object result = entityManager.createQuery(
            "select sum(o.totalAmount) from orders o " +
            "where o.orderDate between :from and :to " +
            "and o.status != 'CANCELLED'")
            .setParameter("from", from)
            .setParameter("to", to)
            .getSingleResult();
        return result != null ? (Double) result : 0.0;
    }

    public Long countOrdersBetween(LocalDate from, LocalDate to) {
        return (Long) entityManager.createQuery(
            "select count(o) from orders o where o.orderDate between :from and :to")
            .setParameter("from", from)
            .setParameter("to", to)
            .getSingleResult();
    }

    // ===== REVENUE REPORT =====

    // Monthly revenue — last 6 months
    public List<Object[]> getMonthlyRevenue() {
        LocalDate since = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        return entityManager.createQuery(
            "select month(o.orderDate), year(o.orderDate), " +
            "sum(o.totalAmount), count(o) " +
            "from orders o " +
            "where o.orderDate >= :since " +
            "and o.status != 'CANCELLED' " +
            "group by year(o.orderDate), month(o.orderDate) " +
            "order by year(o.orderDate), month(o.orderDate)",
            Object[].class)
            .setParameter("since", since)
            .getResultList();
    }

    public Double getTotalRevenueAllTime() {
        Object r = entityManager.createQuery(
            "select sum(o.totalAmount) from orders o where o.status != 'CANCELLED'")
            .getSingleResult();
        return r != null ? (Double) r : 0.0;
    }

    // ===== PRODUCT REPORT =====

    // Product এর উপর বিক্রয় — OrderItem থেকে group by product
    public List<Object[]> getProductSalesData() {
        return entityManager.createQuery(
            "select i.productId, i.productName, " +
            "sum(i.quantity), sum(i.totalPrice) " +
            "from order_item i " +
            "join i.order o " +
            "where o.status != 'CANCELLED' " +
            "group by i.productId, i.productName " +
            "order by sum(i.quantity) desc",
            Object[].class)
            .getResultList();
    }

    public Long getTotalUnitsSold() {
        Object r = entityManager.createQuery(
            "select sum(i.quantity) from order_item i " +
            "join i.order o where o.status != 'CANCELLED'")
            .getSingleResult();
        return r != null ? (Long) r : 0L;
    }

    // ===== CUSTOMER REPORT =====

    // Customer এর total orders ও total spending
    public List<Object[]> getCustomerSpendingData() {
        return entityManager.createQuery(
            "select o.customerName, o.customerPhone, " +
            "count(o), sum(o.totalAmount) " +
            "from orders o " +
            "where o.status != 'CANCELLED' " +
            "group by o.customerName, o.customerPhone " +
            "order by sum(o.totalAmount) desc",
            Object[].class)
            .getResultList();
    }

    // Customer table এর stats
    public Long countTotalCustomers() {
        Object r = entityManager.createQuery("select count(c) from customer c").getSingleResult();
        return r != null ? (Long) r : 0L;
    }

    public Long countActiveCustomers() {
        return (Long) entityManager.createQuery(
            "select count(c) from customer c where c.status = 'active'").getSingleResult();
    }

    public Long countVipCustomers() {
        return (Long) entityManager.createQuery(
            "select count(c) from customer c where c.type = 'vip'").getSingleResult();
    }

    public Long countNewCustomersThisMonth() {
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        return (Long) entityManager.createQuery(
            "select count(c) from customer c where c.createdAt >= :first")
            .setParameter("first", firstOfMonth)
            .getSingleResult();
    }

    // Customer full list with email
    public List<Object[]> getCustomerFullList() {
        return entityManager.createQuery(
            "select c.id, c.name, c.email, c.phone, c.type, c.createdAt " +
            "from customer c order by c.createdAt desc",
            Object[].class)
            .getResultList();
    }
}
