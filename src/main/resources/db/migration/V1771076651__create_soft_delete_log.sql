CREATE TABLE soft_delete_log
(
    id             UUID                    DEFAULT gen_random_uuid() PRIMARY KEY,
    entity_id      VARCHAR(255)           NOT NULL,
    entity_type           VARCHAR(255)   NOT NULL,
    reason         TEXT           NOT NULL,
    deleted_at     TIMESTAMP      NOT NULL DEFAULT now()
);
