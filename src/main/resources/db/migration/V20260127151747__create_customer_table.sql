CREATE TABLE customer
(
    id          UUID    DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    address     VARCHAR(255) NOT NULL,
    cpf         VARCHAR(20)  NOT NULL,
    birth_date  DATE         NOT NULL,
    active      BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX customer_unique_cpf_idx ON customer (cpf)