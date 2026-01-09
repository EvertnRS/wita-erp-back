CREATE TABLE role(
    id INTEGER PRIMARY KEY NOT NULL,
    role VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX unique_role_idx ON role(role);