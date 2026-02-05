CREATE TABLE transaction
(
    id               UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    value            NUMERIC(15, 2) NOT NULL,
    transaction_code VARCHAR(100)   NOT NULL,
    description       TEXT,
    payment_type_id  UUID           NOT NULL,
    active           BOOLEAN                 DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT payment_type_id_fk FOREIGN KEY (payment_type_id) REFERENCES payment_type (id)
);

CREATE UNIQUE INDEX transaction_unique_transaction_code_idx ON transaction(transaction_code);