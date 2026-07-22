package com.mgt.dao;

import com.mgt.model.PasswordResetOtp;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Repository
@Transactional
public class PasswordResetOtpDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(PasswordResetOtp otp) { em.persist(otp); }

    public void update(PasswordResetOtp otp) { em.merge(otp); }

    /** সবচেয়ে latest, unused, non-expired OTP খোঁজো */
    public PasswordResetOtp findValidOtp(String email, String otp) {
        try {
            return em.createQuery(
                "from password_reset_otp where email = :email and otp = :otp " +
                "and used = false and expiresAt > :now order by id desc", PasswordResetOtp.class)
                .setParameter("email", email)
                .setParameter("otp", otp)
                .setParameter("now", LocalDateTime.now())
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /** নতুন OTP তৈরির আগে এই email-এর পুরনো unused OTP গুলো invalidate করো */
    public void invalidateOldOtps(String email) {
        em.createQuery(
            "update password_reset_otp set used = true where email = :email and used = false")
            .setParameter("email", email)
            .executeUpdate();
    }

    /** Rate limiting: গত N মিনিটে কতবার OTP request হয়েছে */
    public long countRecentRequests(String email, LocalDateTime since) {
        return em.createQuery(
            "select count(o) from password_reset_otp o where o.email = :email and o.createdAt > :since",
            Long.class)
            .setParameter("email", email)
            .setParameter("since", since)
            .getSingleResult();
    }
}
