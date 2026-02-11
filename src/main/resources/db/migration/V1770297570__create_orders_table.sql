CREATE TABLE orders
(
    id               UUID PRIMARY KEY,
    discount         NUMERIC(15, 2) NOT NULL,
    seller_id          UUID           NOT NULL,
    installments        INT,
    customer_payment_type_id  UUID           NOT NULL,

    CONSTRAINT customer_payment_type_id_fk FOREIGN KEY (customer_payment_type_id) REFERENCES customer_payment_type (id),
    CONSTRAINT seller_id_fk FOREIGN KEY (seller_id) REFERENCES users (id),
    CONSTRAINT fk_orders FOREIGN KEY (id) REFERENCES transaction(id)
);