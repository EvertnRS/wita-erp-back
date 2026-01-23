INSERT INTO role (role)
VALUES ('ADMIN');

INSERT INTO role_permission_relation (role_id, permission_id)
SELECT r.id, p.id
FROM role r
         CROSS JOIN permission p
WHERE r.role = 'ADMIN';

INSERT INTO users (name, email, password, role_id)
VALUES ('admin', 'admin@example.com', '$2a$10$QP5WNiYnOKetVVj2/Lgqsu7/MhOeE6ozsBB80c6qb9slHtnE9V30a', 1);