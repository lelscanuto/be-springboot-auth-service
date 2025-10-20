-- Insert permissions (privileges)
INSERT INTO permission (name) VALUES
 ('CREATE_USERS'),
 ('FETCH_USERS'),
 ('CREATE_ROLES'),
 ('FETCH_ROLES'),
 ('CREATE_PERMISSIONS'),
 ('READ_PERMISSIONS');

-- Insert ADMIN role
INSERT INTO role (name) VALUES ('ADMIN');

-- Link ADMIN role to all permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p ON TRUE
WHERE r.name = 'ADMIN';

-- Insert default admin user (BCrypt hashed password)
INSERT INTO user_account (username, password, status)
VALUES ('admin', '$2a$10$qYnnX08P1AWaRahZzAsS1e3QkqNxbB95FuY5wsq42Bp8HiiY3d4KK', 1);

-- Link admin user to ADMIN role
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user_account u, role r
WHERE u.username = 'admin' AND r.name = 'ADMIN';