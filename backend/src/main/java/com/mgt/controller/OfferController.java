package com.mgt.controller;

import com.mgt.model.Offer;
import com.mgt.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/offer")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class OfferController {

    @Autowired
    OfferService offerService;

    // ===== ADMIN =====

    // Banner image সহ offer create (multipart)
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("offerType") String offerType,
            @RequestParam("discountPercentage") Double discountPercentage,
            @RequestParam(value = "productId",  required = false) Long productId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "startDate",  required = false) String startDate,
            @RequestParam(value = "endDate",    required = false) String endDate,
            @RequestParam(value = "bannerImage",required = false) MultipartFile bannerImage) {

        offerService.create(title, description, offerType, discountPercentage,
                productId, categoryId, startDate, endDate, bannerImage);
        return ResponseEntity.ok("Offer created successfully");
    }

    @GetMapping("/getall")
    public List<Offer> getAll() {
        return offerService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getById(@PathVariable long id) {
        Offer o = offerService.getById(id);
        return o != null ? ResponseEntity.ok(o) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody Offer offer) {
        offer.setId(id);
        offerService.update(offer);
        return ResponseEntity.ok("Offer updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        offerService.delete(id);
        return ResponseEntity.ok("Offer deleted");
    }

    // ===== CUSTOMER / FRONTEND =====

    // Homepage এ active banner/flash sale দেখানোর জন্য
    @GetMapping("/active")
    public List<Offer> getActiveOffers() {
        return offerService.getActiveOffers();
    }

    @GetMapping("/by-product/{productId}")
    public List<Offer> getByProduct(@PathVariable long productId) {
        return offerService.getOffersByProductId(productId);
    }

    @GetMapping("/by-category/{categoryId}")
    public List<Offer> getByCategory(@PathVariable long categoryId) {
        return offerService.getOffersByCategoryId(categoryId);
    }
}
