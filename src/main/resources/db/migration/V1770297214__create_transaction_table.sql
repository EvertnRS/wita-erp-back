CREATE TABLE transaction
(
    id               UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    value            NUMERIC(15, 2) NOT NULL,
    transaction_code VARCHAR(100)   NOT NULL,
    description       TEXT,
    active           BOOLEAN                 DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT now()

);

CREATE UNIQUE INDEX transaction_unique_transaction_code_idx ON transaction(transaction_code);