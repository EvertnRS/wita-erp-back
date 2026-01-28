CREATE TABLE product_purchase_relation (
    product_id UUID NOT NULL,
    purchase_id UUID NOT NULL,
    quantity INTEGER NOT NULL,

    CONSTRAINT product_purchase_id PRIMARY KEY (product_id, purchase_id),
    CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT purchase_id_fk FOREIGN KEY (purchase_id) REFERENCES purchase(id)
);

CREATE INDEX idx_product_purchase_id ON product_purchase_relation(purchase_id);