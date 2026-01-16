CREATE TABLE IF NOT EXISTS product_sizes(
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    size_id INTEGER NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_product_size_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_size_size FOREIGN KEY (size_id) REFERENCES sizes(id) ON DELETE CASCADE,
    CONSTRAINT chk_product_size_stock CHECK (stock >= 0)
);