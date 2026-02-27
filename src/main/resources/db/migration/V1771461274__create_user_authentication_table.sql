CREATE TABLE users_authentication
(
    id             UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id        UUID           NOT NULL,
    secret           VARCHAR(255),
    temporary_secret           VARCHAR(255),
    temporary_secret_expiration     TIMESTAMP,
    created_at     TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES users(id)
);
