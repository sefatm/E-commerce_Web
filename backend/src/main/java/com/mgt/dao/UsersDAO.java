package com.mgt.dao;

import com.mgt.model.Users;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class UsersDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Users user) {
        em.persist(user);
    }

    public Users findByEmail(String email) {
        if (email == null) return null;
        try {
            return em.createQuery("from users u where lower(u.email) = :email", Users.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Login helper: email অথবা phone দিয়ে user খুঁজবে।
     * Frontend field যদি "Email or Phone Number" হয়, এই method দুটোই support করবে।
     */
    public Users findByEmailOrPhone(String identifier) {
        if (identifier == null) return null;

        String value = identifier.trim();
        if (value.isEmpty()) return null;

        List<String> phones = phoneCandidates(value);

        try {
            return em.createQuery(
                    "from users u where lower(u.email) = :email or u.phone in :phones", Users.class)
                    .setParameter("email", value.toLowerCase())
                    .setParameter("phones", phones)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<String> phoneCandidates(String raw) {
        Set<String> values = new LinkedHashSet<>();
        String v = raw == null ? "" : raw.trim();
        values.add(v);

        String compact = v.replace(" ", "").replace("-", "");
        values.add(compact);

        if (compact.startsWith("+880")) {
            values.add("0" + compact.substring(4));
            values.add(compact.substring(1));
        } else if (compact.startsWith("880")) {
            values.add("0" + compact.substring(3));
            values.add("+" + compact);
        } else if (compact.startsWith("0")) {
            values.add("+88" + compact);
            values.add("88" + compact);
        }
        return new ArrayList<>(values);
    }

    public List<Users> getAll() {
        return em.createQuery("from users order by id desc", Users.class).getResultList();
    }

    public Users getById(long id) {
        return em.find(Users.class, id);
    }

    public void update(Users user) {
        em.merge(user);
    }

    public boolean delete(long id) {
        Users user = em.find(Users.class, id);
        if (user == null) return false;

        // Delete or unlink records that commonly create FK constraint errors.
        // Each query is guarded because some project copies may not have every table.
        safeNative("DELETE FROM wishlist WHERE user_id = ?1", id);
        safeNative("DELETE FROM review WHERE customer_id = ?1", id);
        safeNative("UPDATE seller SET user_id = NULL WHERE user_id = ?1", id);
        safeNative("UPDATE vendor SET user_id = NULL WHERE user_id = ?1", id);

        em.remove(em.contains(user) ? user : em.merge(user));
        return true;
    }

    private void safeNative(String sql, long id) {
        try {
            em.createNativeQuery(sql).setParameter(1, id).executeUpdate();
        } catch (Exception ignored) {
            // Table/column may not exist in older DB versions. Ignore and continue.
        }
    }

    public boolean emailExists(String email) {
        if (email == null) return false;
        Long count = (Long) em.createQuery(
                "select count(u) from users u where lower(u.email) = :email")
                .setParameter("email", email.trim().toLowerCase())
                .getSingleResult();
        return count > 0;
    }

    public boolean emailExistsForOtherUser(String email, long userId) {
        if (email == null) return false;
        Long count = (Long) em.createQuery(
                "select count(u) from users u where lower(u.email) = :email and u.id != :userId")
                .setParameter("email", email.trim().toLowerCase())
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }
}
