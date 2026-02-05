CREATE TABLE purchase
(
    id                UUID           PRIMARY KEY,
    buyer_id         UUID           NOT NULL,
    supplier_id          UUID           NOT NULL,

    CONSTRAINT supplier_id_fk FOREIGN KEY (supplier_id) REFERENCES supplier (id),
    CONSTRAINT buyer_id_fk FOREIGN KEY (buyer_id) REFERENCES users (id),
    CONSTRAINT fk_purchase FOREIGN KEY (id) REFERENCES transaction(id)
);
