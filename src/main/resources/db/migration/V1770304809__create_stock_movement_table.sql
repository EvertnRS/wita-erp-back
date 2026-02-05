CREATE TABLE stock_movement
(
    id                UUID           DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id       UUID           NOT NULL,
    movement_type VARCHAR(50)  NOT NULL,
    quantity          INTEGER        NOT NULL,
    movement_reason_id       UUID           NOT NULL,
    transaction_id             UUID,
    user_id       UUID           NOT NULL,
    created_at        TIMESTAMP      NOT NULL DEFAULT now(),
    active            BOOLEAN        DEFAULT TRUE,

    CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT movement_reason_id_fk FOREIGN KEY (movement_reason_id) REFERENCES movement_reason (id),
    CONSTRAINT transaction_id_fk FOREIGN KEY (transaction_id) REFERENCES transaction (id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);