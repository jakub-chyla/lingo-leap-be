CREATE TABLE IF NOT EXISTS tts_api_keys (
    id BIGSERIAL PRIMARY KEY,
    api_key TEXT NOT NULL UNIQUE,
    usage_date DATE NOT NULL DEFAULT CURRENT_DATE,
    daily_usage INTEGER NOT NULL DEFAULT 0,
    last_used_at TIMESTAMPTZ,
    CONSTRAINT chk_tts_api_keys_daily_usage CHECK (daily_usage BETWEEN 0 AND 10)
);

CREATE INDEX IF NOT EXISTS idx_tts_api_keys_usage
    ON tts_api_keys (usage_date, daily_usage, id);

INSERT INTO tts_api_keys (api_key) VALUES
    ('esk_lmUnfA0gxJpKEii-nNhx_tCzWfoNsIlu'),
    ('esk_BkX4lNSlmDxXDKr6MdxadElxJIQIapp5'),
    ('esk_C2Fs61KYk5oW8qSpTJ9siIeUi4N6cfkK'),
    ('esk_URK5A3qeCkCqjFTYCnEPk-dnf9HHoV1X'),
    ('esk_tKh2QPgW94T0JcYtCI6hiitFhk3K5a-R'),
    ('esk_k7Im1Jfqg3IGcLM57j9pEzNkGrVsKWZG'),
    ('esk_F5KLVUFCrKXRkptRg7ZjVYUdsJ53MueF'),
    ('esk_hHOJmEg8I18UzG0TQSP2lOvbanHFA5oa'),
    ('esk_VeJXvVCpmV9QEJJc3Ij71FJafcq9zzVM'),
    ('esk_giaU2WFB9NtFgRS6WQ2shzwpVj50hHCE'),
    ('esk_Nk4tCkTGW9roInBH5u2QQ6c1wSRQHsob'),
    ('esk_GtUT_7vNZAXe0NkONkCWPj4sV_pzmpE3'),
    ('esk_RbZe2dw6VJO4q7VJekiddyjxeg1-dSyG'),
    ('esk_Tcu6vIx7p3hjnkSSwbLDB_oZ_6A6JlOz');
