CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(50),
    username VARCHAR(50) NOT NULL,
    language_code VARCHAR(4) NOT NULL,
    allows_write_to_pm BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);