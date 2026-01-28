CREATE TABLE purchase
(
    id                UUID           DEFAULT gen_random_uuid() PRIMARY KEY,
    value             NUMERIC(15, 2) NOT NULL,
    supplier_id          UUID           NOT NULL,
    payment_type_id   UUID           NOT NULL,
    active            BOOLEAN        DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT payment_type_id_fk FOREIGN KEY (payment_type_id) REFERENCES payment_type (id),
    CONSTRAINT supplier_id_fk FOREIGN KEY (supplier_id) REFERENCES supplier (id)
);