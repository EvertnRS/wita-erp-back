CREATE TABLE supplier
(
    id                UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    name              VARCHAR(255)   NOT NULL,
    email              VARCHAR(255)   NOT NULL,
    address              VARCHAR(255)   NOT NULL,
    cnpj              VARCHAR(255)   NOT NULL,
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),
    active            BOOLEAN                 DEFAULT TRUE

);

CREATE UNIQUE INDEX supplier_unique_name_idx ON supplier(name)