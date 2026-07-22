package com.mgt.service;

import com.mgt.dao.SellerDAO;
import com.mgt.dao.UsersDAO;
import com.mgt.model.Seller;
import com.mgt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SellerService {

    @Autowired
    private SellerDAO sellerDAO;

    @Autowired
    private UsersDAO usersDAO;

    public Seller apply(Seller seller) {
        seller.setId(0);
        seller.setStatus("PENDING");
        seller.setVerified(false);
        seller.setCreatedAt(LocalDate.now());
        if (seller.getCommissionRate() == null) seller.setCommissionRate(10.0);
        if (seller.getRating() == null) seller.setRating(0.0);
        if (seller.getReviewCount() == null) seller.setReviewCount(0);

        if (seller.getUser() != null && seller.getUser().getId() > 0) {
            Users user = usersDAO.getById(seller.getUser().getId());
            seller.setUser(user);
        }

        sellerDAO.save(seller);
        return seller;
    }

    public List<Seller> getAll() {
        return sellerDAO.getAll();
    }

    public List<Seller> getPending() {
        return sellerDAO.getByStatus("PENDING");
    }

    public List<Seller> getApproved() {
        return sellerDAO.getApproved();
    }

    public Seller getById(long id) {
        return sellerDAO.getById(id);
    }

    public Seller getByUserId(long userId) {
        return sellerDAO.getByUserId(userId);
    }

    public boolean approve(long id) {
        Seller seller = sellerDAO.getById(id);
        if (seller == null) return false;
        seller.setStatus("APPROVED");
        seller.setVerified(true);
        seller.setRejectionReason(null);
        if (seller.getUser() != null) {
            Users user = usersDAO.getById(seller.getUser().getId());
            if (user != null) {
                user.setRole("seller");
                usersDAO.update(user);
                seller.setUser(user);
            }
        }
        sellerDAO.update(seller);
        return true;
    }

    public boolean reject(long id, String reason) {
        Seller seller = sellerDAO.getById(id);
        if (seller == null) return false;
        seller.setStatus("REJECTED");
        seller.setVerified(false);
        seller.setRejectionReason(reason == null || reason.trim().isEmpty()
                ? "Application information could not be verified."
                : reason.trim());
        sellerDAO.update(seller);
        return true;
    }

    public boolean update(long id, Seller updated) {
        Seller seller = sellerDAO.getById(id);
        if (seller == null) 
        	return false;

        if (updated.getName() != null)
        	seller.setName(updated.getName());
        if (updated.getShopName() != null) 
        	seller.setShopName(updated.getShopName());
        if (updated.getNidNo() != null) 
        	seller.setNidNo(updated.getNidNo());
        if (updated.getPhone() != null)
        	seller.setPhone(updated.getPhone());
        if (updated.getEmail() != null) 
        	seller.setEmail(updated.getEmail());
        if (updated.getAddress() != null) 
        	seller.setAddress(updated.getAddress());
        if (updated.getDistrict() != null) 
        	seller.setDistrict(updated.getDistrict());
        if (updated.getArtisanStory() != null) 
        	seller.setArtisanStory(updated.getArtisanStory());
        if (updated.getCraftProcess() != null) 
        	seller.setCraftProcess(updated.getCraftProcess());
        if (updated.getProductCategory() != null)
            seller.setProductCategory(updated.getProductCategory());
        if (updated.getBusinessType() != null)
            seller.setBusinessType(updated.getBusinessType());
        if (updated.getPaymentMethod() != null)
            seller.setPaymentMethod(updated.getPaymentMethod());
        if (updated.getPaymentNumber() != null)
            seller.setPaymentNumber(updated.getPaymentNumber());
        if (updated.getProfilePhoto() != null)
            seller.setProfilePhoto(updated.getProfilePhoto());
        if (updated.getNidFrontImage() != null)
            seller.setNidFrontImage(updated.getNidFrontImage());
        if (updated.getNidBackImage() != null)
            seller.setNidBackImage(updated.getNidBackImage());
        if (updated.getCommissionRate() != null)
        	seller.setCommissionRate(updated.getCommissionRate());

        sellerDAO.update(seller);
        return true;
    }

}
