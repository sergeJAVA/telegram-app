package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.entity.Category;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import com.sergejava.telegram_app.entity.Size;
import com.sergejava.telegram_app.exceptions.CategoryNotFoundException;
import com.sergejava.telegram_app.exceptions.SizeNotFoundByNameException;
import com.sergejava.telegram_app.mapper.ProductMapper;
import com.sergejava.telegram_app.repository.CategoryRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.repository.ProductSizeRepository;
import com.sergejava.telegram_app.repository.SizeRepository;
import com.sergejava.telegram_app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .stock(getTotalStock(request.getSizes()))
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        Set<ProductSize> productSizes = new HashSet<>(getProductSize(request.getSizes(), savedProduct));
        savedProduct.setProductSizes(productSizes);

        return ProductMapper.toDTO(savedProduct);
    }

    private Integer getTotalStock(Map<String, Integer> sizes) {
        int totalStock = 0;
        for (Map.Entry<String, Integer> entry : sizes.entrySet()) {
            totalStock += entry.getValue();
        }
        return totalStock;
    }

    private List<ProductSize> getProductSize(Map<String, Integer> sizes, Product product) {
        Set<ProductSize> productSizes = new HashSet<>();
        for (Map.Entry<String, Integer> entry : sizes.entrySet()) {

            String sizeName = entry.getKey().toUpperCase(Locale.ROOT);
            int sizeStock = entry.getValue();

            Size size = sizeRepository.findByName(sizeName)
                    .orElseThrow(() -> new SizeNotFoundByNameException(sizeName));

            ProductSize productSize = ProductSize.builder()
                    .stock(sizeStock)
                    .product(product)
                    .size(size)
                    .build();
            productSizes.add(productSize);
        }
        return productSizeRepository.saveAll(productSizes);
    }

}
