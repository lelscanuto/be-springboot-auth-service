-- Add domain-specific permissions for student features
INSERT INTO permission (name)
VALUES
 ('VIEW_PROFILE'),
 ('UPDATE_PROFILE'),
 ('ENROLL_COURSE');

-- Create STUDENT role
INSERT INTO role (name) VALUES ('STUDENT');

-- Link STUDENT role to domain-specific permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p ON p.name IN ('VIEW_PROFILE', 'UPDATE_PROFILE', 'ENROLL_COURSE')
WHERE r.name = 'STUDENT';

-- Create a student user (status = 1 → ACTIVE)
INSERT INTO user_account (username, password, status)
VALUES ('student', '$2a$10$hxtn.6CQJp9EFQuDdRmjN.SBOeQDul3LwkENiYNARoEKcV.0tg5Xa', 1);

-- Link student user to STUDENT role
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user_account u, role r
WHERE u.username = 'student' AND r.name = 'STUDENT';

UPDATE role
SET created_at = NOW(), updated_at = NOW(), created_by = 'system', updated_by = 'system'
WHERE name = 'STUDENT';


UPDATE user_account
SET created_at = NOW(), updated_at = NOW(), created_by = 'system', updated_by = 'system'
WHERE username = 'student';



