package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.ProductSizeDTO;
import com.sergejava.telegram_app.entity.Category;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductImage;
import com.sergejava.telegram_app.entity.ProductSize;
import com.sergejava.telegram_app.entity.Size;
import com.sergejava.telegram_app.exceptions.CategoryNotFoundException;
import com.sergejava.telegram_app.exceptions.ImageUrlsNullOrEmptyException;
import com.sergejava.telegram_app.exceptions.InsufficientStockException;
import com.sergejava.telegram_app.exceptions.InvalidImageUrlException;
import com.sergejava.telegram_app.exceptions.SizeNotFoundByNameException;
import com.sergejava.telegram_app.mapper.ProductMapper;
import com.sergejava.telegram_app.repository.CategoryRepository;
import com.sergejava.telegram_app.repository.ProductImageRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.repository.ProductSizeRepository;
import com.sergejava.telegram_app.repository.SizeRepository;
import com.sergejava.telegram_app.service.ProductService;
import com.sergejava.telegram_app.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(getTotalStock(request.getSizes()))
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        Set<ProductSize> productSizes = new HashSet<>(getProductSizes(request.getSizes(), savedProduct));
        savedProduct.setProductSizes(productSizes);

        Set<ProductImage> productImages = new LinkedHashSet<>(getProductImages(request.getImageUrls(), savedProduct));
        savedProduct.setImages(productImages);

        return ProductMapper.toDTO(savedProduct);
    }

    @Override
    public Page<ProductDTO> findByCategoryName(String categoryName, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.byCategoryName(categoryName);
        return productRepository.findAll(spec, pageable).map(ProductMapper::toDTO);
    }

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toDTO);
    }

    @Transactional
    @Override
    public void changeProductSizeStock(Product product, String sizeName, Integer quantity) {
        int totalStock = product.getStock();
        if (totalStock == 0 || totalStock < quantity) {
            throw new InsufficientStockException("the total stock of the product is either zero " +
                    "or the quantity of the requested product is greater than the stock.");
        }
        ProductSize productSize = product.getProductSizes()
                .stream()
                .filter(p -> p.getSize().getName().equals(sizeName))
                .findFirst().orElseThrow(() -> new RuntimeException("ProductSize with size '" + sizeName + "' not found!"));
        int stockSize = productSize.getStock();
        if (stockSize == 0 || stockSize < quantity) {
            throw new InsufficientStockException("The stock of the required product size is 0" +
                    " or the requested quantity is greater than the stock size!");
        }
        stockSize -= quantity;
        totalStock -= quantity;
        productSize.setStock(stockSize);
        product.setStock(totalStock);
    }

    /**
     * Метод для подсчёта количества товаров на складе.
     * @param sizes {@code Map<String, Integer>} тип размера и их количество в формате ключ-значение
     * @return {@code Integer} общее количество всех товаров.
     * @author sergeJAVA
     */
    private Integer getTotalStock(Map<String, Integer> sizes) {
        int totalStock = 0;
        for (Map.Entry<String, Integer> entry : sizes.entrySet()) {
            totalStock += entry.getValue();
        }
        return totalStock;
    }

    /**
     * Метод для создания и возвращения размеров товара.
     * @param sizes Тип размера и их количество в формате ключ-значение.
     * @param product Сущность для связывания с сущностью размера товара {@link ProductSize}.
     * @return возвращает {@code List<ProductSize}.
     * @author sergeJAVA
     */
    private List<ProductSize> getProductSizes(Map<String, Integer> sizes, Product product) {
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

    /**
     * Метод для создания и получения картинок товара.
     * <p>Примечание: первая картинка из списка будет иметь значение {@code true} у поля {@code isMain}.</p>
     * @param imageUrls Ссылки на картинку.
     * @param product Сущность для связывания с сущностью картинки товара {@link ProductImage}.
     * @author sergeJAVA
     */
    private List<ProductImage> getProductImages(List<String> imageUrls, Product product) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new ImageUrlsNullOrEmptyException();
        }
        validateImageUrls(imageUrls);

        Set<ProductImage> images = new LinkedHashSet<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);

            Boolean isMain = (i == 0);

            ProductImage productImage = ProductImage.builder()
                    .url(url)
                    .isMain(isMain)
                    .product(product)
                    .build();
            images.add(productImage);
        }
        return productImageRepository.saveAll(images);
    }

    /**
     * Метод для проверки того, что ссылки на картинки непустые.
     * @param imageUrls ссылки на картинки.
     * @author sergeJAVA
     */
    private void validateImageUrls(List<String> imageUrls) {
        for (String url : imageUrls) {
            if (!StringUtils.hasText(url)) {
                throw new InvalidImageUrlException();
            }
        }
    }

}
