CREATE TABLE IF NOT EXISTS purchases (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS _users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    access_token VARCHAR(255),
    refresh_token VARCHAR(255),
    is_logged_out BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES _users (id) ON DELETE CASCADE
);
