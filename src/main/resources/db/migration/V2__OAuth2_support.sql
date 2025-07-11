ALTER TABLE users
    ADD COLUMN oauth2_provider VARCHAR(50);
ALTER TABLE users
    ADD COLUMN oauth2_provider_id VARCHAR(255);
ALTER TABLE users
    ADD COLUMN name VARCHAR(100);
ALTER TABLE users
    ADD COLUMN image_url VARCHAR(255);

CREATE UNIQUE INDEX idx_oauth2_provider ON users (oauth2_provider, oauth2_provider_id);