ALTER TABLE users
    ADD COLUMN verify_email_token VARCHAR(255),
    ADD COLUMN verify_email_token_expires_at TIMESTAMP;