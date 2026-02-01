CREATE TABLE order_item
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id UUID           NOT NULL,
    order_id   UUID           NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL,
    total      NUMERIC(15, 2) NOT NULL,

    CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id),

    CONSTRAINT unique_product_per_order UNIQUE (order_id, product_id)
);

CREATE INDEX idx_product_order_id ON order_item (order_id);