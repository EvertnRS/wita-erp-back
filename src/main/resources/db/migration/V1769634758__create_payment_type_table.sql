CREATE TABLE payment_type
(
    id                UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    payment_method            VARCHAR(50)           NOT NULL,
    is_immediate            BOOLEAN               NOT NULL,
    allows_installments            BOOLEAN               NOT NULL,
    max_installments        INT                     DEFAULT 1,
    active            BOOLEAN                 DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT now()

);
