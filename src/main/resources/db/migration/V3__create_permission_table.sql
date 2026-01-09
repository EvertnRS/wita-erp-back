CREATE TABLE permission(
    id INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX unique_permission_name_idx ON permission(name);