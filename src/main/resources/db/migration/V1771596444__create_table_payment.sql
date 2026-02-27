CREATE TABLE payment
(
    id                 UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    receivable_id      UUID           NOT NULL,
    gateway            VARCHAR(50)    NOT NULL,
    gateway_payment_id VARCHAR(100)   NOT NULL UNIQUE,
    gateway_session_id VARCHAR(100),
    amount             NUMERIC(15, 2) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    status             VARCHAR(30)    NOT NULL,
    attempts           INTEGER        NOT NULL DEFAULT 0,
    last_event_at      TIMESTAMP WITH TIME ZONE,
    created_at         TIMESTAMP      NOT NULL DEFAULT now(),
    paid_at            TIMESTAMP,

    CONSTRAINT fk_payment_receivable FOREIGN KEY (receivable_id) REFERENCES receivable (id)
);