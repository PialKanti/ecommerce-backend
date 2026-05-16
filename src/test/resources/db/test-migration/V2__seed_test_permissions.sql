INSERT INTO permissions (name, code, description, created_at, modified_at) VALUES
    ('Read Products',      'PERMISSION_PRODUCT_READ',      'View product listings and details.',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Create Products',    'PERMISSION_PRODUCT_CREATE',    'Add new products to the catalog.',            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update Products',    'PERMISSION_PRODUCT_UPDATE',    'Modify existing product details.',            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Delete Products',    'PERMISSION_PRODUCT_DELETE',    'Remove products from the catalog.',           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Read Categories',    'PERMISSION_CATEGORY_READ',     'View product category listings.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Create Categories',  'PERMISSION_CATEGORY_CREATE',   'Add new product categories.',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update Categories',  'PERMISSION_CATEGORY_UPDATE',   'Modify existing product categories.',         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Delete Categories',  'PERMISSION_CATEGORY_DELETE',   'Remove product categories.',                  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Read Inventory',     'PERMISSION_INVENTORY_READ',    'View inventory stock levels.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Manage Inventory',   'PERMISSION_INVENTORY_MANAGE',  'Update inventory stock quantities.',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Read Orders',        'PERMISSION_ORDER_READ',        'View customer order details.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update Orders',      'PERMISSION_ORDER_UPDATE',      'Update order status and details.',            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Cancel Orders',      'PERMISSION_ORDER_CANCEL',      'Cancel customer orders.',                     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Read Users',         'PERMISSION_USER_READ',         'View user account details.',                  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Manage Users',       'PERMISSION_USER_MANAGE',       'Manage user accounts and status.',            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Read Roles',         'PERMISSION_ROLE_READ',         'View role definitions.',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Create Roles',       'PERMISSION_ROLE_CREATE',       'Create new roles.',                           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update Roles',       'PERMISSION_ROLE_UPDATE',       'Modify existing roles.',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Delete Roles',       'PERMISSION_ROLE_DELETE',       'Remove roles.',                               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Assign Roles',       'PERMISSION_ROLE_ASSIGN',       'Assign roles to users.',                      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Read Permissions',   'PERMISSION_READ',              'View permission definitions.',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Create Permissions', 'PERMISSION_CREATE',            'Define new permissions.',                     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update Permissions', 'PERMISSION_UPDATE',            'Modify existing permissions.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Delete Permissions', 'PERMISSION_DELETE',            'Remove permissions.',                         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Assign Permissions', 'PERMISSION_ASSIGN',            'Assign permissions to roles.',                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN';

-- PRODUCT_MANAGER gets product and category permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'PERMISSION_PRODUCT_READ', 'PERMISSION_PRODUCT_CREATE', 'PERMISSION_PRODUCT_UPDATE', 'PERMISSION_PRODUCT_DELETE',
    'PERMISSION_CATEGORY_READ', 'PERMISSION_CATEGORY_CREATE', 'PERMISSION_CATEGORY_UPDATE', 'PERMISSION_CATEGORY_DELETE'
)
WHERE r.code = 'PRODUCT_MANAGER';

-- INVENTORY_MANAGER gets inventory permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('PERMISSION_INVENTORY_READ', 'PERMISSION_INVENTORY_MANAGE')
WHERE r.code = 'INVENTORY_MANAGER';

-- SUPPORT_AGENT gets order read and cancel permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('PERMISSION_ORDER_READ', 'PERMISSION_ORDER_CANCEL')
WHERE r.code = 'SUPPORT_AGENT';
