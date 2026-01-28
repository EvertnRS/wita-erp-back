CREATE TABLE role_permission_relation (
    role_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,

    CONSTRAINT role_permission_id PRIMARY KEY (role_id, permission_id),
    CONSTRAINT role_id_fk FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT permission_id_fk FOREIGN KEY (permission_id) REFERENCES permission(id)
);

CREATE INDEX idx_role_permission_id ON role_permission_relation(permission_id);