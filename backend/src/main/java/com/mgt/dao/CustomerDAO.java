package com.mgt.dao;

import com.mgt.model.Customer;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class CustomerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Customer customer) {
        entityManager.persist(customer);
    }

    public List<Customer> getAll() {
        return entityManager.createQuery(
            "from customer order by createdAt desc", Customer.class
        ).getResultList();
    }

    public Customer getById(long id) {
        return entityManager.find(Customer.class, id);
    }

    public Customer findByEmail(String email) {
        try {
            return entityManager.createQuery(
                "from customer where email = :email", Customer.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void update(Customer customer) {
        entityManager.merge(customer);
    }

    public void delete(long id) {
        entityManager.createQuery("delete from customer where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Long countTotal() {
        return (Long) entityManager.createQuery(
            "select count(c) from customer c").getSingleResult();
    }

    public Long countByStatus(String status) {
        return (Long) entityManager.createQuery(
            "select count(c) from customer c where c.status = :status")
            .setParameter("status", status)
            .getSingleResult();
    }

    public Long countByType(String type) {
        return (Long) entityManager.createQuery(
            "select count(c) from customer c where c.type = :type")
            .setParameter("type", type)
            .getSingleResult();
    }
}
