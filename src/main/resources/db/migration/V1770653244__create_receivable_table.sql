CREATE TABLE receivable
(
    id             UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    value          NUMERIC(15, 2) NOT NULL,
    due_date       TIMESTAMP      NOT NULL,
    payment_status VARCHAR(20)    NOT NULL,
    installment    INTEGER,
    order_id       UUID           NOT NULL,
    paid_at       TIMESTAMP,
    active         BOOLEAN                 DEFAULT TRUE,
    created_at     TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id)

);
