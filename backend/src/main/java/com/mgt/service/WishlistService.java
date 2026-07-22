package com.mgt.service;

import com.mgt.dao.AddProductDAO;
import com.mgt.dao.UsersDAO;
import com.mgt.dao.WishlistDAO;
import com.mgt.model.AddProduct;
import com.mgt.model.Users;
import com.mgt.model.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WishlistService {

    @Autowired
    private WishlistDAO wishlistDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private AddProductDAO productDAO;

    public Wishlist add(long userId, int productId) {
        Wishlist existing = wishlistDAO.getByUserAndProduct(userId, productId);
        if (existing != null) return existing;

        Users user = usersDAO.getById(userId);
        AddProduct product = productDAO.getById(productId);
        if (user == null) throw new RuntimeException("User not found: " + userId);
        if (product == null) throw new RuntimeException("Product not found: " + productId);

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setCreatedAt(LocalDate.now());
        wishlistDAO.save(wishlist);
        return wishlist;
    }

    public boolean remove(long userId, int productId) {
        Wishlist existing = wishlistDAO.getByUserAndProduct(userId, productId);
        if (existing == null) return false;
        wishlistDAO.delete(existing.getId());
        return true;
    }

    public void removeById(long id) {
        wishlistDAO.delete(id);
    }

    public boolean exists(long userId, int productId) {
        return wishlistDAO.getByUserAndProduct(userId, productId) != null;
    }

    public List<Wishlist> getByUserId(long userId) {
        return wishlistDAO.getByUserId(userId);
    }

    public Map<String, Object> toResponse(Wishlist wishlist) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", wishlist.getId());
        data.put("createdAt", wishlist.getCreatedAt());
        data.put("userId", wishlist.getUser() != null ? wishlist.getUser().getId() : null);
        data.put("product", wishlist.getProduct());
        data.put("productId", wishlist.getProduct() != null ? wishlist.getProduct().getId() : null);
        return data;
    }
}
