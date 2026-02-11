CREATE TABLE purchase_item
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id UUID NOT NULL,
    purchase_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL,
    total      NUMERIC(15, 2) NOT NULL,

    CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT purchase_id_fk FOREIGN KEY (purchase_id) REFERENCES purchase(id),
    CONSTRAINT unique_product_per_purchase UNIQUE (purchase_id, product_id)
);

CREATE INDEX idx_product_purchase_id ON purchase_item(purchase_id);