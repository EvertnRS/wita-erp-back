CREATE TABLE payment_type
(
    id                UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    name            VARCHAR(100)           NOT NULL,
    is_immediate            BOOLEAN               NOT NULL,
    allows_installments            BOOLEAN               NOT NULL,
    max_installments        INT                     DEFAULT 1,
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),
    active            BOOLEAN                 DEFAULT TRUE

);
