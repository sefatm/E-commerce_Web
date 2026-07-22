package com.mgt.dao;

import com.mgt.model.Brand;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public class BrandDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Brand brand) { em.persist(brand); }

    public List<Brand> getAll() {
        return em.createQuery("from Brand order by id desc", Brand.class).getResultList();
    }

    public Brand getById(Long id) { return em.find(Brand.class, id); }

    public List<Brand> search(String keyword) {
        String kw = "%" + keyword.toLowerCase() + "%";
        return em.createQuery("from Brand where lower(name) like :kw", Brand.class)
                .setParameter("kw", kw).getResultList();
    }

    public void update(Brand brand) { em.merge(brand); }

    public void delete(Long id) {
        em.createQuery("delete from Brand where id = :id")
                .setParameter("id", id).executeUpdate();
    }
}
