CREATE TABLE payable
(
    id                UUID           DEFAULT gen_random_uuid() PRIMARY KEY,
    value          NUMERIC(15, 2) NOT NULL,
    due_date        TIMESTAMP      NOT NULL,
    payment_status VARCHAR(20)    NOT NULL,
    installment      INTEGER,
    purchase_id   UUID           NOT NULL,
    active            BOOLEAN        DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT purchase_id_fk FOREIGN KEY (purchase_id) REFERENCES purchase (id)

);
