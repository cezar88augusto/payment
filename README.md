# 💰 Desafio Backend - Contas a Pagar

Este projeto consiste na implementação de uma API REST para um sistema simples de **contas a pagar**, atendendo a requisitos técnicos e funcionais definidos em um desafio prático de backend.

A API permite:

- Realizar **CRUD** de contas a pagar.
- **Alterar situação** de uma conta após pagamento.
- **Consultar** contas com filtros e paginação.
- **Importar** um lote de contas a pagar a partir de um **arquivo CSV**.

---

## ✅ Requisitos Gerais

1. Linguagem: **Java 17** ou superior
2. Framework: **Spring Boot**
3. Banco de Dados: **PostgreSQL**
4. Aplicação containerizada com **Docker**
5. Orquestração com **Docker Compose**
6. Código hospedado no **GitHub**
7. Utilizar **mecanismo de autenticação**
8. Organização do projeto seguindo **Domain Driven Design (DDD)**
9. Migrations com **Flyway**
10. Utilizar **JPA**
11. APIs de consulta devem ser **paginadas**

---

## 📋 Requisitos Específicos

### 📌 Estrutura da tabela `bill`

Campos obrigatórios:

- `id`
- `due_date`
- `payment_date`
- `amount`
- `description`
- `status`
- `created_at`
- `updated_at`

### 📌 Funcionalidades da API

- [x] **Cadastrar conta**
- [x] **Atualizar conta**
- [x] **Alterar situação da conta**
- [x] **Listar contas com filtro de data de vencimento e descrição (com paginação)**
- [x] **Buscar conta por ID**
- [x] **Obter valor total pago em um determinado período**
- [x] **Importar contas via arquivo CSV codificado em Base64 (via API)**

---

## 🧪 Testes

- [x] Implementação de **testes unitários** cobrindo regras de negócio e controle de fluxo.

---
