package com.mgt.dao;
import com.mgt.model.SellerWallet;
import org.springframework.stereotype.Repository;
import javax.persistence.*;
import javax.transaction.Transactional;
@Repository @Transactional
public class SellerWalletDAO {
 @PersistenceContext private EntityManager em;
 public SellerWallet getBySeller(long sellerId){ java.util.List<SellerWallet> r=em.createQuery("from seller_wallet where seller.id=:id",SellerWallet.class).setParameter("id",sellerId).setMaxResults(1).getResultList(); return r.isEmpty()?null:r.get(0);}
 public SellerWallet save(SellerWallet w){ if(w.getId()==0){em.persist(w);return w;} return em.merge(w);}
}
