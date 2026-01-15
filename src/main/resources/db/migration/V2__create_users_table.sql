CREATE TABLE users(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    role_id INTEGER NOT NULL,

    CONSTRAINT role_id_fk FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE UNIQUE INDEX user_unique_email_idx ON users(email)