CREATE TABLE category
(
    id     UUID    DEFAULT gen_random_uuid() PRIMARY KEY,
    name   VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE UNIQUE INDEX category_unique_name_idx ON category (name)