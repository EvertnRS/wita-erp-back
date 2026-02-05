CREATE TABLE orders
(
    id               UUID PRIMARY KEY ,
    discount         NUMERIC(15, 2) NOT NULL,
    customer_id      UUID           NOT NULL,
    seller_id          UUID           NOT NULL,

    CONSTRAINT customer_id_fk FOREIGN KEY (customer_id) REFERENCES customer (id),
    CONSTRAINT seller_id_fk FOREIGN KEY (seller_id) REFERENCES users (id),
    CONSTRAINT fk_orders FOREIGN KEY (id) REFERENCES transaction(id)
);