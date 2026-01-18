INSERT INTO permission (name, description) VALUES
-- Usuários
('USER_READ', 'Visualizar usuários'),
('USER_CREATE', 'Criar usuários'),
('USER_UPDATE', 'Atualizar usuários'),
('USER_DELETE', 'Remover usuários'),

-- Roles
('ROLE_READ', 'Visualizar perfis'),
('ROLE_CREATE', 'Criar perfis'),
('ROLE_UPDATE', 'Atualizar perfis'),
('ROLE_DELETE', 'Remover perfis'),

-- Produtos
('PRODUCT_READ', 'Visualizar produtos'),
('PRODUCT_CREATE', 'Criar produtos'),
('PRODUCT_UPDATE', 'Atualizar produtos'),
('PRODUCT_DELETE', 'Remover produtos'),

-- Pedidos
('ORDER_READ', 'Visualizar pedidos'),
('ORDER_CREATE', 'Criar pedidos'),
('ORDER_UPDATE', 'Atualizar pedidos'),
('ORDER_CANCEL', 'Cancelar pedidos'),

-- Compras
('PURCHASE_READ', 'Visualizar compras'),
('PURCHASE_CREATE', 'Criar compras'),
('PURCHASE_UPDATE', 'Atualizar compras'),
('PURCHASE_DELETE', 'Remover compras'),

-- Clientes
('CUSTOMER_READ', 'Visualizar clientes'),
('CUSTOMER_CREATE', 'Criar clientes'),
('CUSTOMER_UPDATE', 'Atualizar clientes'),
('CUSTOMER_DELETE', 'Remover clientes'),

-- Fornecedor
('SUPPLIER_READ', 'Visualizar fornecedores'),
('SUPPLIER_CREATE', 'Criar fornecedores'),
('SUPPLIER_UPDATE', 'Atualizar fornecedores'),
('SUPPLIER_DELETE', 'Remover fornecedores'),

-- Categorias
('CATEGORY_READ', 'Visualizar categorias de produtos'),
('CATEGORY_CREATE', 'Criar categorias de produtos'),
('CATEGORY_UPDATE', 'Atualizar categorias de produtos'),
('CATEGORY_DELETE', 'Remover categorias de produtos'),

-- Relatórios
('REPORT_READ', 'Visualizar relatórios'),
('REPORT_EXPORT', 'Exportar relatórios');
