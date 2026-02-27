CREATE TABLE payment_gateway_customer
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id         UUID         NOT NULL,
    gateway             VARCHAR(30)  NOT NULL,
    gateway_customer_id VARCHAR(100) NOT NULL UNIQUE,

    CONSTRAINT fk_pgc_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);