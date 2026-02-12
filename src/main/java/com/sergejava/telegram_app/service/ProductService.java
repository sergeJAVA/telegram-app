package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * <strong>Интерфейс, предоставляющий методы, для работы с бизнес-логикой над сущностью {@link Product}.</strong>
 *
 * @author sergeJAVA
 */
public interface ProductService {

    /**
     * <b>Метод для создания нового товара, у которого задаётся размер, ссылки на картинки и категория.</b>
     *
     * @param request {@link CreateProductRequest} запрос, приходящий из контроллера.
     * @return {@link ProductDTO}
     *
     * @author sergeJAVA
     */
    ProductDTO createProduct(CreateProductRequest request);

    /**
     * <b>Метод для получения товаров по категории с пагинацией.</b>
     *
     * @param categoryName название категории.
     * @param pageable передается {@link PageRequest}.
     * @return {@code Page<ProductDTO>}
     *
     * @author sergeJAVA
     */
    Page<ProductDTO> findByCategoryName(String categoryName, Pageable pageable);

    /**
     * <b>Метод для получения всех товаров с пагинацией.</b>
     *
     * @param pageable передается {@link PageRequest}.
     * @return {@code Page<ProductDTO>}
     *
     * @author sergeJAVA
     */
    Page<ProductDTO> findAll(Pageable pageable);

    /**
     * <b>Метод для уменьшения количества товара с определённым размером на складе.</b>
     *
     * @param product сущность, у которой будем изменять значение поля {@code stock}.
     * @param sizeName название размера (S, M, L и т.д.).
     * @param quantity количество, на которое хотим уменьшить значение {@code stock}.
     * @return {@link ProductSize}
     *
     * @author sergeJAVA
     */
    ProductSize reduceProductSizeStock(Product product, String sizeName, Integer quantity);

}
