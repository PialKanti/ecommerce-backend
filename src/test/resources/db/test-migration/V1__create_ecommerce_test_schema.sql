CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    code VARCHAR(50) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    price DOUBLE PRECISION NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BIGINT NOT NULL,
    image_url VARCHAR(250),
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id)
);

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

CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE UNIQUE INDEX uk_carts_user_id ON carts (user_id);

CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts (id),
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id)
        REFERENCES products (id),
    CONSTRAINT uk_cart_product
        UNIQUE (cart_id, product_id),
    CONSTRAINT chk_cart_items_quantity_positive
        CHECK (quantity > 0),
    CONSTRAINT chk_cart_items_unit_price_positive
        CHECK (unit_price > 0)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number UUID NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT chk_orders_status
        CHECK (status IN ('CREATED', 'CONFIRMED', 'PAID', 'CANCELLED')),
    CONSTRAINT chk_orders_total_amount_non_negative
        CHECK (total_amount >= 0),
    CONSTRAINT chk_orders_cancelled_at_required_when_cancelled
        CHECK (
            (status = 'CANCELLED' AND cancelled_at IS NOT NULL)
            OR
            (status <> 'CANCELLED' AND cancelled_at IS NULL)
        )
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    quantity INTEGER NOT NULL,
    total_price DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id),
    CONSTRAINT chk_order_items_quantity_positive
        CHECK (quantity > 0),
    CONSTRAINT chk_order_items_unit_price_positive
        CHECK (unit_price > 0),
    CONSTRAINT chk_order_items_total_price_positive
        CHECK (total_price > 0)
);

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

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(30),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE CASCADE
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions (id)
        ON DELETE CASCADE
);

INSERT INTO roles (name, code, description, created_at, modified_at)
VALUES
    ('Administrator', 'ADMIN', 'Full platform administrator with all permissions.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Customer', 'CUSTOMER', 'Default shopper role for registered customers.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Product Manager', 'PRODUCT_MANAGER', 'Manages product and category catalog operations.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Inventory Manager', 'INVENTORY_MANAGER', 'Manages product inventory and stock movements.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Support Agent', 'SUPPORT_AGENT', 'Supports customer order lookup and cancellation workflows.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
