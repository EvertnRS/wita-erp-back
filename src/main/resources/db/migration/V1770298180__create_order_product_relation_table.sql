CREATE TABLE orders_item
(
    id         UUID  PRIMARY KEY,
    product_id UUID           NOT NULL,
    orders_id   UUID           NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL,
    total      NUMERIC(15, 2) NOT NULL,

    CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT orders_id_fk FOREIGN KEY (orders_id) REFERENCES orders (id),
    CONSTRAINT unique_product_per_order UNIQUE (orders_id, product_id)

);

CREATE INDEX idx_product_orders_id ON orders_item (orders_id);