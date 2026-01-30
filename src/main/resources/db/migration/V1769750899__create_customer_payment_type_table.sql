CREATE TABLE customer_payment_type
(
    id UUID PRIMARY KEY,
    supports_refunds BOOLEAN NOT NULL,

    CONSTRAINT fk_customer_payment_type FOREIGN KEY (id) REFERENCES payment_type(id)
);
