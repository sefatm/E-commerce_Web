package com.mgt.dao;

import com.mgt.model.Settings;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class SettingsDAO {

    @PersistenceContext
    private EntityManager em;

    // সব settings load
    public List<Settings> getAll() {
        return em.createQuery("from settings order by section, settingKey", Settings.class).getResultList();
    }

    // একটা section এর সব settings
    public List<Settings> getBySection(String section) {
        return em.createQuery("from settings where section = :section", Settings.class)
                .setParameter("section", section).getResultList();
    }

    // একটা specific key
    public Settings getByKey(String section, String key) {
        try {
            return em.createQuery(
                    "from settings where section = :section and settingKey = :key", Settings.class)
                    .setParameter("section", section)
                    .setParameter("key", key)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // Upsert: থাকলে update, না থাকলে insert
    public void upsert(String section, String key, String value) {
        Settings existing = getByKey(section, key);
        if (existing != null) {
            existing.setSettingValue(value);
            em.merge(existing);
        } else {
            em.persist(new Settings(section, key, value));
        }
    }

    public void delete(Long id) {
        em.createQuery("delete from settings where id = :id")
                .setParameter("id", id).executeUpdate();
    }
}
