INSERT INTO role (role)
VALUES ('ADMIN');

INSERT INTO role_permission_relation (role_id, permission_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8);

INSERT INTO users (name, email, password, role_id)
VALUES ('admin', 'admin@example.com', '$2a$10$QP5WNiYnOKetVVj2/Lgqsu7/MhOeE6ozsBB80c6qb9slHtnE9V30a', 1);