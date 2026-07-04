CREATE TABLE inventories (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    total_quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT fk_inventories_product
        FOREIGN KEY (product_id)
        REFERENCES products (id),
    CONSTRAINT chk_inventories_total_quantity_non_negative
        CHECK (total_quantity >= 0),
    CONSTRAINT chk_inventories_reserved_quantity_non_negative
        CHECK (reserved_quantity >= 0),
    CONSTRAINT chk_inventories_reserved_quantity_not_above_total
        CHECK (reserved_quantity <= total_quantity)
);

CREATE INDEX idx_inventories_product_id ON inventories (product_id);
