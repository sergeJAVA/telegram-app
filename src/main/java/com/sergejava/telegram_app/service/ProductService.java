package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;

public interface ProductService {

    ProductDTO createProduct(CreateProductRequest request);

}
