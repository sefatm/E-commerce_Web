package com.mgt.dao;

import com.mgt.model.Vendor;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public class VendorDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Vendor vendor) { em.persist(vendor); }

    // ✅ BUG FIX #3: HQL Entity Name Bug
    // ─────────────────────────────────────────────────────────
    // আগের সমস্যা: VendorDAO এর সব HQL query তে "seller" ব্যবহার হচ্ছিল:
    //   "from seller order by id desc"        ← ভুল
    //   "from seller where status = :status"  ← ভুল
    //   "update seller set status..."          ← ভুল
    //   "delete from seller where id..."       ← ভুল
    //
    // HQL এ entity name হল @Entity(name="...") এর value।
    // Vendor.java তে @Entity(name="vendor") — তাই HQL এ "vendor" ব্যবহার করতে হবে।
    // "seller" লিখলে Hibernate Seller.java entity খুঁজবে, Vendor নয়।
    // ফলে vendor এর data load হতো না বা exception আসতো।
    // ─────────────────────────────────────────────────────────

    public List<Vendor> getAll() {
        // ✅ FIXED: "from seller" → "from vendor"
        return em.createQuery("from vendor order by id desc", Vendor.class).getResultList();
    }

    public List<Vendor> getByStatus(String status) {
        // ✅ FIXED: "from seller where..." → "from vendor where..."
        return em.createQuery("from vendor where status = :status order by id desc", Vendor.class)
                .setParameter("status", status).getResultList();
    }

    public Vendor getById(Long id) {
        return em.find(Vendor.class, id);
    }

    public List<Vendor> search(String keyword) {
        String kw = "%" + keyword.toLowerCase() + "%";
        // ✅ FIXED: "from seller where..." → "from vendor where..."
        return em.createQuery(
                "from vendor where lower(name) like :kw " +
                "or lower(shopName) like :kw " +
                "or lower(email) like :kw " +
                "or lower(phone) like :kw order by id desc", Vendor.class)
                .setParameter("kw", kw).getResultList();
    }

    public Long countByStatus(String status) {
        return (Long) em.createQuery("select count(v) from vendor v where v.status = :status")
                .setParameter("status", status).getSingleResult();
    }

    public Long totalCount() {
        // ✅ FIXED: "from seller v" → "from vendor v"
        return (Long) em.createQuery("select count(v) from vendor v").getSingleResult();
    }

    public void update(Vendor vendor) { em.merge(vendor); }

    public void updateStatus(Long id, String status) {
        // ✅ FIXED: "update seller set status..." → "update vendor set status..."
        em.createQuery("update vendor set status = :status where id = :id")
                .setParameter("status", status).setParameter("id", id).executeUpdate();
    }

    public void updateCommission(Long id, Double rate) {
        // ✅ FIXED: "update seller set commissionRate..." → "update vendor set commissionRate..."
        em.createQuery("update vendor set commissionRate = :rate where id = :id")
                .setParameter("rate", rate).setParameter("id", id).executeUpdate();
    }

    public void delete(Long id) {
        // ✅ FIXED: "delete from seller where id..." → "delete from vendor where id..."
        em.createQuery("delete from vendor where id = :id").setParameter("id", id).executeUpdate();
    }
}
