package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.annotation.ValidPageable;
import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable String categoryName,
            @ValidPageable @RequestParam(defaultValue = "0") int page,
            @ValidPageable(type = "size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.findByCategoryName(categoryName, PageRequest.of(page, size)));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAll(
            @ValidPageable @RequestParam(defaultValue = "0") int page,
            @ValidPageable(type = "size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.findAll(PageRequest.of(page, size)));
    }

}
