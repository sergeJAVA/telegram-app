CREATE TABLE IF NOT EXISTS product_images(
    id BIGSERIAL PRIMARY KEY,
    url TEXT NOT NULL,
    is_main BOOLEAN DEFAULT FALSE,
    product_id BIGINT NOT NULL,

    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);