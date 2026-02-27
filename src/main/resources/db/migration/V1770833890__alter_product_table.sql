ALTER TABLE product
ADD supplier_id UUID NOT NULL,
ADD CONSTRAINT supplier_id_fk FOREIGN KEY (supplier_id) REFERENCES supplier (id);