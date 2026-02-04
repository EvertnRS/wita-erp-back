CREATE TABLE order
(
    id               UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    value            NUMERIC(15, 2) NOT NULL,
    discount         NUMERIC(15, 2) NOT NULL,
    transaction_code VARCHAR(100)   NOT NULL,
    description       TEXT,
    customer_id      UUID           NOT NULL,
    seller_id          UUID           NOT NULL,
    payment_type_id  UUID           NOT NULL,
    active           BOOLEAN                 DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT payment_type_id_fk FOREIGN KEY (payment_type_id) REFERENCES payment_type (id),
    CONSTRAINT customer_id_fk FOREIGN KEY (customer_id) REFERENCES customer (id),
    CONSTRAINT seller_id_fk FOREIGN KEY (seller_id) REFERENCES users (id)
);

CREATE UNIQUE INDEX order_unique_transaction_code_idx ON order (transaction_code);