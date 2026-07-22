package com.mgt.controller;

import com.mgt.model.ApiResponse;
import com.mgt.model.Brand;
import com.mgt.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/brand")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class BrandController {

    @Autowired BrandService brandService;

    // POST /brand/create
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody Brand brand) {
        if (brand.getName() == null || brand.getName().trim().isEmpty())
            return ResponseEntity.badRequest().body(ApiResponse.error("Brand name is required"));
        brandService.create(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Brand created successfully"));
    }

    // GET /brand/getall
    // GET /brand/getall?search=keyword
    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAll(@RequestParam(required = false) String search) {
        List<Brand> brands = (search != null && !search.trim().isEmpty())
                ? brandService.search(search)
                : brandService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Brands fetched", brands));
    }

    // GET /brand/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        Brand brand = brandService.getById(id);
        if (brand == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Brand not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Brand fetched", brand));
    }

    // PUT /brand/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Brand brand) {
        boolean updated = brandService.update(id, brand);
        if (!updated) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Brand not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Brand updated"));
    }

    // DELETE /brand/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted = brandService.delete(id);
        if (!deleted) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Brand not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok("Brand deleted"));
    }
}
