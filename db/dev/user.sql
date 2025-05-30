-- user
CREATE TABLE users
(
    id                      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    uuid                    UUID UNIQUE  NOT NULL,
    username                VARCHAR(255) UNIQUE NOT NULL,
    password                VARCHAR(255) NOT NULL,
    enabled                 BOOLEAN      NOT NULL DEFAULT true
);

