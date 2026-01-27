CREATE TABLE IF NOT EXISTS order_items(
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_size VARCHAR(10) NOT NULL,
    image_url TEXT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    CONSTRAINT chk_quantity CHECK (quantity >= 1),
    CONSTRAINT chk_price CHECK (price >= 0),
    CONSTRAINT order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);