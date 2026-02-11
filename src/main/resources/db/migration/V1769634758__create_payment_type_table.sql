CREATE TABLE payment_type
(
    id                UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    is_immediate            BOOLEAN               NOT NULL,
    allows_installments            BOOLEAN               NOT NULL,
    active            BOOLEAN                 DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT now()

);
