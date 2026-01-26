CREATE TABLE IF NOT EXISTS orders(
    id BIGSERIAL PRIMARY KEY,
    total_price NUMERIC(10, 2) NOT NULL,
    status VARCHAR(15) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(12),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL,

    CONSTRAINT orders_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_total_price CHECK (total_price >= 0)
);