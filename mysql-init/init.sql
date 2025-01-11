CREATE DATABASE IF NOT EXISTS APP_MAIN;
CREATE DATABASE IF NOT EXISTS APP_AUDIT;

USE APP_MAIN;

CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    record_status VARCHAR(10) NOT NULL DEFAULT 'Active', -- Active, Deleted
    correlation_id VARCHAR(255)
);

CREATE TABLE address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_type VARCHAR(50) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    record_status VARCHAR(10) NOT NULL DEFAULT 'Active', -- Active, Deleted
    correlation_id VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

USE APP_AUDIT;

CREATE TABLE IF NOT EXISTS audit_trail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_id BIGINT NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    old_value JSON,
    new_value JSON,
    correlation_id VARCHAR(255),
    op_code VARCHAR(50) NOT NULL,
    audit_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    deduplication_id VARCHAR(255)
);
