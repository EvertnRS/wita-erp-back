# ERP Comercial

## Sobre o Projeto

O **ERP Comercial** é um sistema de gestão empresarial desenvolvido com o objetivo de atender **pequenas e médias empresas do ramo de comércio varejista**. O sistema centraliza e automatiza processos essenciais como **controle de estoque, vendas, financeiro, compras e relatórios**, reduzindo o uso de planilhas e minimizando erros operacionais.

Este projeto está sendo desenvolvido como uma **iniciativa acadêmica e prática**, simulando um cenário real de mercado, onde empresas possuem dificuldades em manter controle e visibilidade do negócio devido à falta de sistemas integrados.

---

## Objetivo do Sistema

O principal objetivo do ERP Comercial é:

- Centralizar as informações da empresa em um único sistema 
- Automatizar processos manuais 
- Melhorar o controle de estoque e vendas
- Oferecer visão clara do financeiro
- Auxiliar na tomada de decisões através de relatórios

---

## Público-Alvo

- Pequenas e médias empresas do **comércio varejista**
- Lojas físicas
- Lojas com vendas presenciais e por canais digitais (WhatsApp, telefone, etc.)

---

## Funcionalidades Principais

- Autenticação e controle de usuários
- Cadastro de produtos e categorias
- Controle de estoque (entradas e saídas)
- Registro de vendas
- Controle de caixa e financeiro
- Cadastro de fornecedores e compras
- Relatórios gerenciais

---

## Requisitos Funcionais

### Autenticação e Usuários
- [ ] Login com usuário e senha
- [ ] Cadastro de usuários
- [ ] Perfis de acesso (Administrador, Gerente, Caixa, Estoquista)
- [ ] Controle de permissões
- [ ] Registro de ações dos usuários

### Produtos
- [ ] Cadastro de produtos
- [ ] Controle de preços
- [ ] Definição de estoque mínimo
- [ ] Ativação e desativação de produtos

### Estoque
- [ ] Registro de entrada de produtos
- [ ] Registro de saída de produtos
- [ ] Atualização automática do estoque
- [ ] Alerta de estoque baixo
- [ ] Inventário de estoque

### Vendas
- [ ] Registro de vendas
- [ ] Venda com múltiplos produtos
- [ ] Aplicação de descontos conforme permissão
- [ ] Registro da forma de pagamento
- [ ] Emissão de comprovante de venda

### Financeiro
- [ ] Controle de contas a pagar
- [ ] Controle de contas a receber
- [ ] Fechamento de caixa diário
- [ ] Geração de fluxo de caixa
- [ ] Registro de despesas operacionais

### Compras e Fornecedores
- [ ] Cadastro de fornecedores
- [ ] Registro de pedidos de compra
- [ ] Associação da compra ao estoque
- [ ] Sugestão de reposição de produtos

### Relatórios
- [ ] Relatório de vendas por período
- [ ] Relatório de produtos mais vendidos
- [ ] Relatório de estoque
- [ ] Relatório financeiro
- [ ] Exportação de relatórios (PDF/Excel)

---

## Regras de Negócio

- [ ] Uma venda só pode ser finalizada se houver estoque disponível
- [ ] Apenas usuários autorizados podem aplicar descontos acima do limite
- [ ] O fechamento de caixa deve ser realizado diariamente
- [ ] Produtos com estoque abaixo do mínimo devem ser sinalizados
- [ ] Toda venda deve gerar movimentação financeira

---

## Requisitos Não Funcionais

- Interface simples e intuitiva
- Sistema responsivo (desktop e tablet)
- Controle de acesso por perfil
- Senhas armazenadas de forma segura
- Backup automático dos dados

---

## Tecnologias

- Backend: Java + Spring Boot
- Frontend: React ou HTML/CSS/JavaScript
- Banco de Dados: PostgreSQL ou MySQL
- Arquitetura: Camadas (Controller, Service, Repository)

---

## Equipe

Projeto desenvolvido em equipe como parte de um estudo prático de **Engenharia de Software**, com foco em análise de requisitos, modelagem e desenvolvimento de sistemas ERP.

---

