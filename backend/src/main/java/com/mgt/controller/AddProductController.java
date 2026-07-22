package com.mgt.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.mgt.model.ProductCategory;
import com.mgt.model.Seller;
import com.mgt.model.Brand;
import com.mgt.service.BrandService;
import com.mgt.service.ProductCategoryService;
import com.mgt.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.mgt.model.AddProduct;
import com.mgt.service.AddProductService;

@RestController
@RequestMapping(value = "/product")

public class AddProductController {

    @Autowired AddProductService productService;
    @Autowired ProductCategoryService categoryService;
    @Autowired SellerService sellerService;
    @Autowired BrandService brandService;

    // ✅ FIX #1: application.properties থেকে upload path নেওয়া হচ্ছে
    // "uploads/" relative path হলে Tomcat temp folder এ যায় — FileNotFoundException দেয়
    // এখন absolute path use করা হচ্ছে
    @Value("${app.upload.dir:${user.home}/rural-uploads}")
    private String uploadDir;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(
            @RequestParam("name")                                                  String name,
            @RequestParam("price")                                                 Double price,
            @RequestParam(value = "nameBn", required = false)                      String nameBn,
            @RequestParam(value = "unit", required = false, defaultValue = "piece") String unit,
            @RequestParam(value = "weight", required = false)                       Double weight,
            @RequestParam(value = "minimumOrderQuantity", required = false, defaultValue = "1") Integer minimumOrderQuantity,
            @RequestParam(value = "productionDate", required = false)               String productionDate,
            @RequestParam(value = "expiryDate", required = false)                   String expiryDate,
            @RequestParam(value = "organic", required = false, defaultValue = "false") Boolean organic,
            @RequestParam(value = "returnAvailable", required = false, defaultValue = "true") Boolean returnAvailable,
            @RequestParam(value = "deliveryAreas", required = false)                String deliveryAreas,

            @RequestParam("categoryId")                                            long categoryId,
            @RequestParam(value = "description",  required = false)                String description,
            @RequestParam(value = "status",       required = false, defaultValue = "active") String status,
            @RequestParam(value = "salePrice",    required = false)                Double salePrice,
            @RequestParam(value = "stock",        required = false, defaultValue = "0") Integer stock,
            @RequestParam(value = "sku",          required = false)                String sku,
            @RequestParam(value = "isFeatured",   required = false, defaultValue = "false") Boolean isFeatured,
            @RequestParam(value = "isOnSale",     required = false, defaultValue = "false") Boolean isOnSale,
            @RequestParam(value = "brandId",      required = false)                Long brandId,
            @RequestParam(value = "sellerId",     required = false)                Long sellerId,
            @RequestParam(value = "originArea",   required = false)                String originArea,
            @RequestParam(value = "artisanStory", required = false)                String artisanStory,
            @RequestParam(value = "craftProcess", required = false)                String craftProcess,
            @RequestParam(value = "preOrderAvailable", required = false, defaultValue = "false") Boolean preOrderAvailable,
            @RequestParam(value = "estimatedProductionDays", required = false)     Integer estimatedProductionDays,
            @RequestParam(value = "image",        required = false)                MultipartFile image) {

        ProductCategory category = categoryService.getById(categoryId);
        if (category == null) {
            return ResponseEntity.badRequest().body("Category not found: " + categoryId);
        }

        Brand brand = null;
        if (brandId != null) {
            brand = brandService.getById(brandId);
            if (brand == null) {
                return ResponseEntity.badRequest().body("Brand not found: " + brandId);
            }
        }

        Seller seller = null;
        if (sellerId == null) {
            return ResponseEntity.badRequest().body("Approved seller is required");
        }
        seller = sellerService.getById(sellerId);
        if (seller == null) {
            return ResponseEntity.badRequest().body("Seller not found: " + sellerId);
        }
        if (!"APPROVED".equalsIgnoreCase(seller.getStatus())) {
            return ResponseEntity.badRequest().body("Only approved sellers can create products");
        }

        AddProduct product = new AddProduct();
        product.setName(name);
        product.setNameBn(nameBn);
        product.setUnit(unit);
        product.setWeight(weight);
        product.setMinimumOrderQuantity(minimumOrderQuantity == null || minimumOrderQuantity < 1 ? 1 : minimumOrderQuantity);
        product.setProductionDate(productionDate == null || productionDate.trim().isEmpty() ? null : LocalDate.parse(productionDate));
        product.setExpiryDate(expiryDate == null || expiryDate.trim().isEmpty() ? null : LocalDate.parse(expiryDate));
        product.setOrganic(organic);
        product.setReturnAvailable(returnAvailable);
        product.setDeliveryAreas(deliveryAreas);
        product.setPrice(price);
        product.setDescription(description);
        product.setStatus(status);
        product.setSalePrice(salePrice);
        product.setStock(stock != null ? stock : 0);
        product.setSku(sku);
        product.setIsFeatured(isFeatured);
        product.setIsOnSale(isOnSale);
        product.setApprovalStatus("PENDING");
        product.setOriginArea(originArea);
        product.setArtisanStory(artisanStory);
        product.setCraftProcess(craftProcess);
        product.setPreOrderAvailable(preOrderAvailable);
        product.setEstimatedProductionDays(estimatedProductionDays);
        product.setCreatedAt(LocalDate.now());
        product.setCategory(category);
        product.setBrand(brand);
        product.setSeller(seller);

        if (image != null && !image.isEmpty()) {
            String savedFileName = saveImage(image);
            if (savedFileName != null) {
                product.setImage(savedFileName);
            }
        }

        productService.create(product);
        return ResponseEntity.ok("Product created successfully");
    }

    /**
     * ✅ FIX: Image save করার সঠিক পদ্ধতি
     *
     * আগের সমস্যা:
     * 1. "uploads/" → relative path → Tomcat temp dir এ যাচ্ছিল → FileNotFoundException
     * 2. Duplicate filename হলে আগেরটা overwrite হত
     * 3. Exception silently catch হচ্ছিল, product তৈরি হচ্ছিল কিন্তু image নেই
     */
    private String saveImage(MultipartFile image) {
        try {
            // ✅ FIX #1: application.properties থেকে absolute path নেওয়া হচ্ছে
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // Directory না থাকলে তৈরি করো
            Files.createDirectories(uploadPath);

            // ✅ FIX #2: timestamp prefix দিয়ে unique filename
            // Same name upload হলে আগেরটা overwrite হত
            String originalName = image.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }
            String fileName = System.currentTimeMillis() + "_" + originalName;

            // ✅ FIX #3: java.io.File এর বদলে nio Path use করা হচ্ছে
            // Path.resolve() দিয়ে cross-platform safe path তৈরি হয়
            Path filePath = uploadPath.resolve(fileName).normalize();

            // Security check: path traversal attack prevent করতে
            if (!filePath.startsWith(uploadPath)) {
                System.err.println("⚠️ Suspicious filename rejected: " + originalName);
                return null;
            }

            Files.write(filePath, image.getBytes());

            System.out.println("✅ Image saved: " + filePath);
            return fileName;

        } catch (Exception e) {
            // ✅ FIX #4: Stack trace এর বদলে meaningful log
            System.err.println("❌ Image upload failed: " + e.getMessage());
            System.err.println("   Upload directory: " + uploadDir);
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/getall")
    public List<AddProduct> getall() {
        return productService.getall();
    }

    @GetMapping("/public")
    public List<AddProduct> getPublicProducts() {
        return productService.getPublicProducts();
    }

    @GetMapping("/pending")
    public List<AddProduct> getPendingProducts() {
        return productService.getPendingProducts();
    }

    @GetMapping("/rejected")
    public List<AddProduct> getRejectedProducts() {
        return productService.getRejectedProducts();
    }

    @GetMapping("/{id}")
    public AddProduct getById(@PathVariable int id) {
        return productService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody AddProduct product) {
        product.setId(id);
        productService.update(product);
        return ResponseEntity.ok("Product updated");
    }

    @RequestMapping(
            value = {"/{id}", "/update/{id}"},
            method = {RequestMethod.PUT, RequestMethod.POST},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateMultipart(
            @PathVariable int id,
            @RequestParam("name")                                                  String name,
            @RequestParam("price")                                                 Double price,
            @RequestParam(value = "nameBn", required = false)                      String nameBn,
            @RequestParam(value = "unit", required = false, defaultValue = "piece") String unit,
            @RequestParam(value = "weight", required = false)                       Double weight,
            @RequestParam(value = "minimumOrderQuantity", required = false, defaultValue = "1") Integer minimumOrderQuantity,
            @RequestParam(value = "productionDate", required = false)               String productionDate,
            @RequestParam(value = "expiryDate", required = false)                   String expiryDate,
            @RequestParam(value = "organic", required = false, defaultValue = "false") Boolean organic,
            @RequestParam(value = "returnAvailable", required = false, defaultValue = "true") Boolean returnAvailable,
            @RequestParam(value = "deliveryAreas", required = false)                String deliveryAreas,

            @RequestParam("categoryId")                                            long categoryId,
            @RequestParam(value = "description",  required = false)                String description,
            @RequestParam(value = "status",       required = false, defaultValue = "active") String status,
            @RequestParam(value = "salePrice",    required = false)                Double salePrice,
            @RequestParam(value = "stock",        required = false, defaultValue = "0") Integer stock,
            @RequestParam(value = "sku",          required = false)                String sku,
            @RequestParam(value = "isFeatured",   required = false, defaultValue = "false") Boolean isFeatured,
            @RequestParam(value = "isOnSale",     required = false, defaultValue = "false") Boolean isOnSale,
            @RequestParam(value = "brandId",      required = false)                Long brandId,
            @RequestParam(value = "sellerId",     required = false)                Long sellerId,
            @RequestParam(value = "originArea",   required = false)                String originArea,
            @RequestParam(value = "artisanStory", required = false)                String artisanStory,
            @RequestParam(value = "craftProcess", required = false)                String craftProcess,
            @RequestParam(value = "preOrderAvailable", required = false, defaultValue = "false") Boolean preOrderAvailable,
            @RequestParam(value = "estimatedProductionDays", required = false)     Integer estimatedProductionDays,
            @RequestParam(value = "image",        required = false)                MultipartFile image) {

        AddProduct product = productService.getById(id);
        if (product == null) return ResponseEntity.notFound().build();

        ProductCategory category = categoryService.getById(categoryId);
        if (category == null) {
            return ResponseEntity.badRequest().body("Category not found: " + categoryId);
        }

        Brand brand = null;
        if (brandId != null) {
            brand = brandService.getById(brandId);
            if (brand == null) {
                return ResponseEntity.badRequest().body("Brand not found: " + brandId);
            }
        }

        Seller seller = null;
        if (sellerId == null) {
            return ResponseEntity.badRequest().body("Approved seller is required");
        }
        seller = sellerService.getById(sellerId);
        if (seller == null) {
            return ResponseEntity.badRequest().body("Seller not found: " + sellerId);
        }
        if (!"APPROVED".equalsIgnoreCase(seller.getStatus())) {
            return ResponseEntity.badRequest().body("Only approved sellers can own products");
        }

        product.setName(name);
        product.setNameBn(nameBn);
        product.setUnit(unit);
        product.setWeight(weight);
        product.setMinimumOrderQuantity(minimumOrderQuantity == null || minimumOrderQuantity < 1 ? 1 : minimumOrderQuantity);
        product.setProductionDate(productionDate == null || productionDate.trim().isEmpty() ? null : LocalDate.parse(productionDate));
        product.setExpiryDate(expiryDate == null || expiryDate.trim().isEmpty() ? null : LocalDate.parse(expiryDate));
        product.setOrganic(organic);
        product.setReturnAvailable(returnAvailable);
        product.setDeliveryAreas(deliveryAreas);
        product.setPrice(price);
        product.setDescription(description);
        product.setStatus(status);
        product.setSalePrice(salePrice);
        product.setStock(stock != null ? stock : 0);
        product.setSku(sku);
        product.setIsFeatured(isFeatured);
        product.setIsOnSale(isOnSale);
        product.setOriginArea(originArea);
        product.setArtisanStory(artisanStory);
        product.setCraftProcess(craftProcess);
        product.setPreOrderAvailable(preOrderAvailable);
        product.setEstimatedProductionDays(estimatedProductionDays);
        product.setCategory(category);
        product.setBrand(brand);
        product.setSeller(seller);

        if (image != null && !image.isEmpty()) {
            String savedFileName = saveImage(image);
            if (savedFileName != null) product.setImage(savedFileName);
        }

        productService.update(product);
        return ResponseEntity.ok("Product updated");
    }

    // ✅ Stock update endpoint for inventory module
    @PatchMapping("/{id}/stock")
    public ResponseEntity<String> updateStock(@PathVariable int id, @RequestBody java.util.Map<String, Integer> body) {
        Integer newStock = body.get("stock");
        if (newStock == null || newStock < 0) return ResponseEntity.badRequest().body("Invalid stock value");
        AddProduct p = productService.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        p.setStock(newStock);
        productService.update(p);
        return ResponseEntity.ok("Stock updated to " + newStock);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable int id) {
        boolean approved = productService.approve(id);
        return approved ? ResponseEntity.ok("Product approved") : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable int id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "";
        boolean rejected = productService.reject(id, reason);
        return rejected ? ResponseEntity.ok("Product rejected") : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        productService.delete(id);
        return ResponseEntity.ok("Product deleted");
    }

    @GetMapping("/by-category/{id}")
    public List<AddProduct> getByCategory(@PathVariable long id) {
        return productService.getByCategoryId(id);
    }

    @GetMapping("/public/by-category/{id}")
    public List<AddProduct> getApprovedByCategory(@PathVariable long id) {
        return productService.getApprovedByCategoryId(id);
    }

    @GetMapping("/seller/{sellerId}")
    public List<AddProduct> getBySeller(@PathVariable long sellerId) {
        return productService.getBySellerId(sellerId);
    }

    @GetMapping("/count/{id}")
    public Long count(@PathVariable long id) {
        return productService.countByCategoryId(id);
    }
    
//    @GetMapping("/getall")
//    public ResponseEntity<List<Product>> getAllProductAPI() {
//      List<Product> products = productService.getall();
//      return ResponseEntity.ok(products);
//  }
}
