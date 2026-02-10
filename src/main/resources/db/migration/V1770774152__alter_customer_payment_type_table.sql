ALTER TABLE customer_payment_type

    ADD COLUMN customer_id            UUID                   NOT NULL,
    ADD CONSTRAINT customer_id_fk FOREIGN KEY (customer_id) REFERENCES customer (id)