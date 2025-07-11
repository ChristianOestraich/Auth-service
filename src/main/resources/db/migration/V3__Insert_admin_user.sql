INSERT INTO users (email, password, enabled, name, created_at, updated_at)
VALUES ('usuario@exemplo.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMy.MZHbjS2X6QHuY7pCw6ZUV1WeF7Wj7O2',
        TRUE,
        'Christian',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Assign ADMIN role to the user
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'usuario@exemplo.com'),
        (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));