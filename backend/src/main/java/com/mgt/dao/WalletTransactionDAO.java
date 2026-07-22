package com.mgt.dao;
import com.mgt.model.WalletTransaction;
import org.springframework.stereotype.Repository;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
@Repository @Transactional
public class WalletTransactionDAO {
 @PersistenceContext private EntityManager em;
 public void save(WalletTransaction t){em.persist(t);}
 public List<WalletTransaction> bySeller(long id){return em.createQuery("from wallet_transaction where seller.id=:id order by createdAt desc, id desc",WalletTransaction.class).setParameter("id",id).getResultList();}
 public boolean exists(String refType,long refId,String type){Long c=em.createQuery("select count(id) from wallet_transaction where referenceType=:rt and referenceId=:ri and type=:t",Long.class).setParameter("rt",refType).setParameter("ri",refId).setParameter("t",type).getSingleResult();return c!=null&&c>0;}
}
