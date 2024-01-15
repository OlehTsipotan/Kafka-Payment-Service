CREATE SEQUENCE IF NOT EXISTS customer_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE customer
(
    id                BIGINT       NOT NULL,
    name              VARCHAR(255) NOT NULL,
    balance_available BIGINT       NOT NULL,
    balance_reserved  BIGINT       NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);