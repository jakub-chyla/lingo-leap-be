CREATE TABLE IF NOT EXISTS settings (
    user_id BIGINT PRIMARY KEY,
    most_wrong INTEGER NOT NULL DEFAULT 0,
    least_answered INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_settings_user FOREIGN KEY (user_id) REFERENCES _users (id) ON DELETE CASCADE
);
