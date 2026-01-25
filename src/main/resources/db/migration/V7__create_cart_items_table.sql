CREATE TABLE IF NOT EXISTS cart_items(
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL DEFAULT 1,
    price NUMERIC(10,2) NOT NULL DEFAULT 0,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_size_id BIGINT NOT NULL,

    CONSTRAINT chk_price CHECK (price >= 0),
    CONSTRAINT chk_quantity CHECK (quantity >= 1),
    CONSTRAINT fk_cart_items_carts FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product_sizes FOREIGN KEY (product_size_id) REFERENCES product_sizes(id) ON DELETE CASCADE
);