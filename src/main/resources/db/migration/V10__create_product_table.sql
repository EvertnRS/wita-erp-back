CREATE TABLE product
(
    id                UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    name              VARCHAR(255)   NOT NULL,
    price             NUMERIC(15, 2) NOT NULL,
    min_quantity      INTEGER        NOT NULL,
    quantity_in_stock INTEGER        NOT NULL,
    active            BOOLEAN                 DEFAULT TRUE,
    category_id       UUID           NOT NULL,
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT category_id_fk FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE UNIQUE INDEX product_unique_name_idx ON product (name)