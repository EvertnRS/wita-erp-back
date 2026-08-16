# 🚀 Wita ERP Back-end

> Um sistema de ERP robusto desenvolvido em Java com Spring Boot para gestão integrada de estoque, transações financeiras e controle de usuários.

## 📋 Sobre o projeto

O Wita ERP Back-end é o motor central de um sistema de gestão empresarial completo, projetado para otimizar processos operacionais e financeiros. O sistema centraliza o controle de inventário, fluxo de caixa (contas a pagar e receber), pedidos, compras e usuários.

* **O que o projeto faz:** Gerencia todo o ciclo de vida de produtos, clientes, fornecedores e transações financeiras, com suporte a pagamentos via gateway.
* **Para quem foi desenvolvido:** Empresas que necessitam de um controle centralizado, seguro e escalável de suas operações comerciais.
* **Qual problema resolve:** Resolve a fragmentação de dados operacionais, automatizando o controle de estoque e a conciliação financeira.
* **Principais diferenciais:** Integração com Stripe para pagamentos, autenticação com 2FA, logs de auditoria (SoftDeleteLog) e agendamento de tarefas via Redis.

### 🎯 Objetivos

* Centralizar a gestão administrativa e financeira;
* Automatizar o controle de estoque e reposição de produtos;
* Garantir segurança robusta com autenticação e autorização baseada em papéis (RBAC).

---

## ✨ Funcionalidades

* ✅ Gestão de Usuários, Cargos (Roles) e Permissões;
* ✅ Controle de Estoque com Histórico de Movimentações;
* ✅ Gestão de Pedidos (Vendas) e Compras;
* ✅ Fluxo Financeiro (Contas a Pagar e Receber);
* ✅ Integração completa com Stripe (Checkout e Webhooks);
* ✅ Agendamento de tarefas (Redis);
* ✅ Relatórios Gerenciais exportáveis;
* ✅ Auditoria de dados (Soft Delete Logging).

---

## 🛠️ Tecnologias utilizadas

### Back-end
* [Java 17/21](https://www.oracle.com/java/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Security](https://spring.io/projects/spring-security)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [Flyway](https://flywaydb.org/) (Migrations)

### Banco de dados
* [PostgreSQL](https://www.postgresql.org/)
* [Redis](https://redis.io/) (Cache/Scheduling)

### Ferramentas & DevOps
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)
* [SonarQube](https://www.sonarqube.org/) (Code Quality)
* [GitHub Actions](https://github.com/features/actions) (CI/CD)

---

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

* [Git](https://git-scm.com/)
* [Java JDK 17 ou superior](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/) e Docker Compose

Verifique as versões:

```bash
git --version
java -version
mvn -version
```

## 🚀 Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/usuario/wita-erp-back.git
cd wita-erp-back
```

### 2. Compile e construa o projeto

Utilize o Maven para baixar as dependências e gerar o artefato da aplicação.

```bash
mvn clean install
```

### 3. Configure as variáveis de ambiente

Crie o arquivo `.env` a partir do arquivo de exemplo:

```bash
cp example.env .env
```

Configure o arquivo `.env`:
```bash
DB_USER=postgres
DB_PASSWORD=sua-senha-secreta
DB_URL=jdbc:postgresql://localhost:5432/wita_erp

JWT_SECRET=sua-chave-jwt-super-secreta

REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=sua-senha-redis

MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-email
MAIL_APP_PASSWORD=sua-app-password

ADMIN_EMAIL=admin@wita.com
ADMIN_PASSWORD=sua-senha-admin

FRONTEND_URL=http://localhost:5173
BACKEND_URL=http://localhost:8080


STRIPE_SECRET_KEY=sk_test_sua_chave_secreta
STRIPE_PUBLIC_KEY=pk_test_sua_chave_publica
STRIPE_WEBHOOK_SECRET=whsec_seu_webhook_secret
```

### 4. Execute a aplicação

Inicie a infraestrutura utilizando Docker Compose:

```bash
docker compose up -d
```

---

## 📖 Como usar

### API REST

A API pode ser consumida através de ferramentas como:

* Postman
* Insomnia
* Thunder Client
* Aplicações web ou mobile

Entre as operações disponíveis estão:

* autenticação;
* gerenciamento de usuários;
* gerenciamento de fluxo financeiro;

### Swagger:

acesse:

http://localhost:3000/api-docs

Na interface do Swagger, é possível:

- Visualizar todas as rotas disponíveis;
- Consultar os parâmetros e corpos das requisições;
- Visualizar os formatos das respostas;
- Autenticar utilizando um JWT através do botão Authorize;
- Executar as requisições diretamente pela interface.
- Autenticação

As rotas protegidas utilizam autenticação Bearer JWT.

Após realizar o login pela rota:

copie o token retornado e clique em Authorize no Swagger.

---

## 🧪 Testes

Execute os testes automatizados com Maven:

```bash
mvn test
```

Para executar também as verificações de build:

```bash
mvn verify
```

---

## 📁 Estrutura do projeto

A aplicação segue uma arquitetura organizada por camadas, separando domínio, serviços, infraestrutura e controladores.

```text
wita-erp-back/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── wita/
│       │           └── erp/
│       │               ├── controllers/   # Endpoints da API
│       │               ├── domain/        # Entidades, DTOs e Mappers
│       │               ├── services/      # Regras de negócio e Observers
│       │               ├── infra/         # Providers e Exceptions
│       │               └── ErpApplication.java
│       └── resources/
│           ├── db/
│           │   └── migration/              # Scripts SQL do Flyway
│           └── templates/                  # Templates de e-mail
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

Consulte o arquivo `LICENSE` para obter o texto completo da licença.

## 🤝 Contribuindo

Contribuições são bem-vindas!

Antes de contribuir:

* siga as diretrizes descritas em `CONTRIBUTING.md`;
* utilize **Conventional Commits**;
* mantenha o padrão de código existente;
* adicione testes quando necessário.

### Convenção de commits

Este projeto utiliza Conventional Commits.

### Licença das contribuições

Ao contribuir com este projeto, você concorda que suas contribuições serão disponibilizadas sob os termos da licença vigente do repositório.

## ⭐ Apoie o projeto

Se este projeto foi útil para você:

* ⭐ Dê uma estrela no repositório.
* 🐛 Reporte problemas.
* 💡 Sugira melhorias.
* 🤝 Contribua com código.
* 📢 Compartilhe o projeto.

Obrigado pelo apoio! ❤️

## 📞 Suporte

Encontrou algum problema?

Abra uma **Issue** informando:

* descrição do problema;
* passos para reprodução;
* comportamento esperado;
* comportamento atual;
* logs ou mensagens de erro;
* sistema operacional;
* versão do Node.js;
* versão do projeto.

## 📚 Documentação

* **README:** documentação e instalação do projeto.
* **`CONTRIBUTING.md`:** guia para contribuição.
* **`LICENSE`:** licença do projeto.

---

## 👥 Equipe

## Equipe
<a href="https://github.com/EvertnRS">
  <img width="100" height="100" alt="¨" al" src="https://github.com/user-attachments/assets/840eeb2e-2866-4a83-a86b-e97a498bde9f" />
</a>


<a href="https://github.com/devictor8">
  <img width="100" height="100" alt="image" src="https://github.com/user-attachments/assets/058be29c-5b3c-4bb4-9733-52955ac98b4f" />
</a>

<a href="https://github.com/CarlosWinicius">
  <img width="100" height="100" alt="image" src="https://github.com/user-attachments/assets/0b34870c-3af6-44a2-bf6b-c256061965dc" />
</a>

<a href="https://github.com/ok-kioo">
  <img width="100" height="100" alt="image" src="https://github.com/user-attachments/assets/f7da043e-005d-4c5c-a4ab-75fdec3ed861" />
</a>

---