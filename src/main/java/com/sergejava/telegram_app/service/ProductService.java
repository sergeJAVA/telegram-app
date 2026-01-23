package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.ProductSizeDTO;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Интерфейс, предоставляющий методы, для работы с бизнес-логикой над сущностью {@link Product}.
 * @author sergeJAVA
 */
public interface ProductService {

    /**
     * Метод для создания нового товара, у которого задаётся размер, ссылки на картинки и категория.
     * @param request {@link CreateProductRequest} запрос, приходящий из контроллера.
     * @return {@link ProductDTO}
     * @author sergeJAVA
     */
    ProductDTO createProduct(CreateProductRequest request);

    /**
     * Метод для получения товаров по категории с пагинацией.
     * @param categoryName название категории.
     * @param pageable передается {@link PageRequest}.
     * @return {@code Page<ProductDTO>}
     * @author sergeJAVA
     */
    Page<ProductDTO> findByCategoryName(String categoryName, Pageable pageable);

    /**
     * Метод для получения всех товаров с пагинацией.
     * @param pageable передается {@link PageRequest}.
     * @return {@code Page<ProductDTO>}
     * @author sergeJAVA
     */
    Page<ProductDTO> findAll(Pageable pageable);

    void changeProductSizeStock(Product product, String sizeName, Integer quantity);

}
