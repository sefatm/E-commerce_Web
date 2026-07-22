package com.mgt.service;

import com.mgt.dao.OfferDAO;
import com.mgt.model.AddProduct;
import com.mgt.model.Offer;
import com.mgt.model.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Service
public class OfferService {

    @Autowired
    OfferDAO offerDAO;

    @Autowired
    AddProductService productService;

    @Autowired
    ProductCategoryService categoryService;

    public void create(String title, String description, String offerType,
                       Double discountPercentage, Long productId, Long categoryId,
                       String startDate, String endDate, MultipartFile bannerImage) {

        Offer offer = new Offer();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setOfferType(offerType);
        offer.setDiscountPercentage(discountPercentage);
        offer.setCreatedAt(LocalDate.now());
        offer.setStatus("ACTIVE");

        if (startDate != null && !startDate.isEmpty())
            offer.setStartDate(LocalDate.parse(startDate));
        if (endDate != null && !endDate.isEmpty())
            offer.setEndDate(LocalDate.parse(endDate));

        if (productId != null) {
            AddProduct product = productService.getById(productId.intValue());
            offer.setProduct(product);
        }
        if (categoryId != null) {
            ProductCategory category = categoryService.getById(categoryId);
            offer.setCategory(category);
        }

        if (bannerImage != null && !bannerImage.isEmpty()) {
            try {
                String uploadDir = "uploads/offers/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = System.currentTimeMillis() + "_" + bannerImage.getOriginalFilename();
                bannerImage.transferTo(new File(uploadDir + fileName));
                offer.setBannerImage(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        offerDAO.save(offer);
    }

    public List<Offer> getAll() { return offerDAO.getAll(); }

    public Offer getById(long id) { return offerDAO.getById(id); }

    public List<Offer> getActiveOffers() { return offerDAO.getActiveOffers(); }

    public List<Offer> getOffersByProductId(long productId) {
        return offerDAO.getOffersByProductId(productId);
    }

    public List<Offer> getOffersByCategoryId(long categoryId) {
        return offerDAO.getOffersByCategoryId(categoryId);
    }

    public void update(Offer offer) { offerDAO.update(offer); }

    public void delete(long id) { offerDAO.delete(id); }
}
