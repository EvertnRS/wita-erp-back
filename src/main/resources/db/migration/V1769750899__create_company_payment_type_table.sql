CREATE TABLE company_payment_type
(
    id UUID PRIMARY KEY,
    bank_code VARCHAR(10) NOT NULL,
    agency VARCHAR(20) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    last_four_digits VARCHAR(4),
    brand VARCHAR(20),
    closing_day VARCHAR(2),

    CONSTRAINT fk_company_payment_type FOREIGN KEY (id) REFERENCES payment_type(id)
);
