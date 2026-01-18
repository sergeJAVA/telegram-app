package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;

public interface ProductService {

    /**
     * Метод для создания нового товара, у которого задаётся размер, ссылки на картинки и категория.
     * @param request {@link CreateProductRequest} запрос, приходящий из контроллера.
     * @return {@link ProductDTO}
     * @author sergeJAVA
     */
    ProductDTO createProduct(CreateProductRequest request);

}
