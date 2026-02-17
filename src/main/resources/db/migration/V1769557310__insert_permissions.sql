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
('REPORT_EXPORT', 'Exportar relatórios'),

-- Estoque
('STOCK_READ', 'Visualizar movimentação de estoque'),
('STOCK_CREATE', 'Ajustar movimentação de estoque'),
('STOCK_UPDATE', 'Atualizar movimentação de estoque'),
('STOCK_DELETE', 'Deletar movimentação de estoque'),

-- Motivo da movimentação
('REASON_READ', 'Visualizar motivo da movimentação'),
('REASON_CREATE', 'Ajustar motivo da movimentação'),
('REASON_UPDATE', 'Atualizar motivo da movimentação'),
('REASON_DELETE', 'Deletar motivo da movimentação'),

-- Tipo de pagamento do cliente
('CUSTOMER_PAYMENT_READ', 'Visualizar tipo de pagamento'),
('CUSTOMER_PAYMENT_CREATE', 'Criar tipo de pagamento'),
('CUSTOMER_PAYMENT_UPDATE', 'Atualizar tipo de pagamento'),
('CUSTOMER_PAYMENT_DELETE', 'Remover tipo de pagamento'),

-- Tipo de pagamento da empresa
('COMPANY_PAYMENT_READ', 'Visualizar tipo de pagamento'),
('COMPANY_PAYMENT_CREATE', 'Criar tipo de pagamento'),
('COMPANY_PAYMENT_UPDATE', 'Atualizar tipo de pagamento'),
('COMPANY_PAYMENT_DELETE', 'Remover tipo de pagamento'),

-- Contas a pagar
('PAYABLE_READ', 'Visualizar contas a pagar'),
('PAYABLE_CREATE', 'Criar contas a pagar'),
('PAYABLE_UPDATE', 'Atualizar contas a pagar'),
('PAYABLE_DELETE', 'Remover contas a pagar'),

-- Contas a receber
('RECEIVABLE_READ', 'Visualizar contas a receber'),
('RECEIVABLE_CREATE', 'Criar contas a receber'),
('RECEIVABLE_UPDATE', 'Atualizar contas a receber'),
('RECEIVABLE_DELETE', 'Remover contas a receber'),

-- Histórico de registros
('LOG_READ', 'Visualizar histórico de registros');
