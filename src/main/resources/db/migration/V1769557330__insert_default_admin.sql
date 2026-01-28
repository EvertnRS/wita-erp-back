INSERT INTO role (role)
VALUES ('ADMIN');

INSERT INTO role_permission_relation (role_id, permission_id)
SELECT r.id, p.id
FROM role r
         CROSS JOIN permission p
WHERE r.role = 'ADMIN';

INSERT INTO users (name, email, password, role_id)
VALUES ('admin', '${admin_email}', '${admin_password_hash}', 1);