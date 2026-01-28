CREATE TABLE movement_reason
(
    id               UUID           DEFAULT gen_random_uuid() PRIMARY KEY,
    reason           VARCHAR(255)   NOT NULL,
    active           BOOLEAN                 DEFAULT TRUE

);

CREATE UNIQUE INDEX movement_reason_unique_name_idx ON movement_reason (reason)