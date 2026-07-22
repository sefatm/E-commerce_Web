package com.mgt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mgt.model.ProductCategory;
import com.mgt.service.ProductCategoryService;

@RestController
@RequestMapping(value = "/category")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*:*"})
public class CategoryController {

    @Autowired
    ProductCategoryService categoryService;

    @PostMapping("/create")
    public void create(@RequestBody ProductCategory category) {
        categoryService.createCategory(category);
    }

    @GetMapping("/getall")
    public List<ProductCategory> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public ProductCategory getById(@PathVariable long id) {
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody ProductCategory category) {
        category.setId(id);
        categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        categoryService.delete(id);
    }
}
