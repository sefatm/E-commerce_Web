package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.Seller;
import com.mgt.model.Users;
import com.mgt.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/seller")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @Value("${app.upload.dir:${user.home}/rural-uploads}")
    private String uploadDir;

    @PostMapping(value = "/apply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> apply(@RequestBody Seller seller) {
        ApiResponse validation = validateSeller(seller);
        if (!validation.isSuccess()) {
            return ResponseEntity.badRequest().body(validation);
        }
        Seller saved = sellerService.apply(seller);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Seller application submitted", saved));
    }

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> applyMultipart(
            @RequestParam("name") String name,
            @RequestParam("shopName") String shopName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("nidNo") String nidNo,
            @RequestParam("district") String district,
            @RequestParam("address") String address,
            @RequestParam(value = "productCategory", required = false) String productCategory,
            @RequestParam(value = "businessType", required = false) String businessType,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
            @RequestParam(value = "paymentNumber", required = false) String paymentNumber,
            @RequestParam(value = "artisanStory", required = false) String artisanStory,
            @RequestParam(value = "craftProcess", required = false) String craftProcess,
            @RequestParam(value = "userId", required = false, defaultValue = "0") long userId,
            @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile,
            @RequestParam(value = "nidFrontFile", required = false) MultipartFile nidFrontFile,
            @RequestParam(value = "nidBackFile", required = false) MultipartFile nidBackFile) {

        Seller seller = new Seller();
        seller.setName(trim(name));
        seller.setShopName(trim(shopName));
        seller.setEmail(trim(email));
        seller.setPhone(trim(phone));
        seller.setNidNo(trim(nidNo));
        seller.setDistrict(trim(district));
        seller.setAddress(trim(address));
        seller.setProductCategory(trim(productCategory));
        seller.setBusinessType(trim(businessType));
        seller.setPaymentMethod(trim(paymentMethod));
        seller.setPaymentNumber(trim(paymentNumber));
        seller.setArtisanStory(trim(artisanStory));
        seller.setCraftProcess(trim(craftProcess));
        if (userId > 0) {
            Users user = new Users();
            user.setId(userId);
            seller.setUser(user);
        }

        ApiResponse validation = validateSeller(seller);
        if (!validation.isSuccess()) {
            return ResponseEntity.badRequest().body(validation);
        }
        if (profilePhotoFile == null || profilePhotoFile.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Profile photo is required"));
        }
        if (nidFrontFile == null || nidFrontFile.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("NID front photo is required"));
        }

        try {
            seller.setProfilePhoto(saveFile(profilePhotoFile, "sellers/profile"));
            seller.setNidFrontImage(saveFile(nidFrontFile, "sellers/nid"));
            if (nidBackFile != null && !nidBackFile.isEmpty()) {
                seller.setNidBackImage(saveFile(nidBackFile, "sellers/nid"));
            }
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File upload failed. Please upload JPG/PNG/PDF under 3MB."));
        }

        Seller saved = sellerService.apply(seller);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Seller application submitted", saved));
    }

    private ApiResponse validateSeller(Seller seller) {
        if (seller == null) return ApiResponse.error("Seller information is required");
        if (isBlank(seller.getName())) return ApiResponse.error("Seller name is required");
        if (isBlank(seller.getShopName())) return ApiResponse.error("Shop or company name is required");
        if (isBlank(seller.getEmail())) return ApiResponse.error("Email is required");
        if (isBlank(seller.getPhone())) return ApiResponse.error("Phone is required");
        if (isBlank(seller.getNidNo())) return ApiResponse.error("NID number is required");
        if (isBlank(seller.getAddress())) return ApiResponse.error("Address is required");
        if (isBlank(seller.getDistrict())) return ApiResponse.error("District is required");
        return ApiResponse.ok("Valid");
    }

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        validateUpload(file);
        Path dir = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Files.write(dir.resolve(fileName), file.getBytes());
        return subDir + "/" + fileName;
    }

    private void validateUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IOException("Empty file");
        if (file.getSize() > 3L * 1024L * 1024L) throw new IOException("File too large");
        String type = file.getContentType();
        if (type == null || !(type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/jpg") || type.equals("application/pdf"))) {
            throw new IOException("Invalid file type");
        }
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String trim(String value) { return value == null ? null : value.trim(); }

    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Sellers fetched", sellerService.getAll()));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPending() {
        return ResponseEntity.ok(ApiResponse.ok("Pending sellers fetched", sellerService.getPending()));
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse> getApproved() {
        return ResponseEntity.ok(ApiResponse.ok("Approved sellers fetched", sellerService.getApproved()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable long id) {
        Seller seller = sellerService.getById(id);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Seller fetched", seller));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getByUser(@PathVariable long userId) {
        Seller seller = sellerService.getByUserId(userId);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller profile not found for user: " + userId));
        }
        return ResponseEntity.ok(ApiResponse.ok("Seller fetched", seller));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable long id, @RequestBody Seller seller) {
        boolean updated = sellerService.update(id, seller);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Seller updated"));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable long id) {
        boolean approved = sellerService.approve(id);
        if (!approved) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Seller approved"));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "";
        boolean rejected = sellerService.reject(id, reason);
        if (!rejected) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Seller not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Seller rejected"));
    }
}
