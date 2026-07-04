CREATE TABLE payment_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    payment_link VARCHAR(2048) NOT NULL,
    status VARCHAR(30) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT fk_payment_history_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id),
    CONSTRAINT chk_payment_history_status
        CHECK (status IN ('INITIATED', 'SUCCESS', 'FAILED', 'CANCELLED'))
);

CREATE INDEX idx_payment_history_order_id ON payment_history (order_id);
CREATE INDEX idx_payment_history_status ON payment_history (status);
CREATE INDEX idx_payment_history_status_expires_at ON payment_history (status, expires_at);
CREATE INDEX idx_payment_history_order_status ON payment_history (order_id, status);
