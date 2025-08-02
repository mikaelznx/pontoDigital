# Livro Ponto Digital

Sistema de ponto eletrônico para controle de jornada de funcionários, com backend em **Spring Boot** e frontend em **Vue.js**. Inclui autenticação JWT, geração de relatórios PDF e funcionalidades de cadastro, edição e consulta de pontos.

---

## 🚀 Tecnologias utilizadas

- **Backend:** Java 17, Spring Boot, Spring Security, JWT, Hibernate, PostgreSQL, JasperReports  
- **Frontend:** Vue.js 3, Axios, HTML5, CSS3  
- **Banco de dados:** PostgreSQL  
- **Build & Package:** Maven (backend), npm (frontend)  

---

## 🎯 Funcionalidades principais

- Cadastro, listagem e edição de funcionários  
- Registro e edição dos pontos (entrada/saída) com data e hora  
- Autenticação via JWT com controle de acesso por roles (usuário/admin)  
- Geração de relatórios mensais em PDF para cada funcionário  
- Frontend moderno com Vue.js, permitindo gerenciar dados e visualizar relatórios  

---

## ⚙️ Configuração e execução

### Pré-requisitos

- Java 17+  
- Maven  
- Node.js (v16+ recomendado)  
- PostgreSQL instalado e rodando  

---

### Banco de dados

1. Crie o banco PostgreSQL:

```sql
CREATE DATABASE livro_ponto;

    Configure o usuário e senha no application.properties do backend.

Rodando o backend

    Navegue até a pasta backend:

cd backend

    Compile e rode o projeto:

mvn clean spring-boot:run

O backend estará disponível em: http://localhost:8080/api
Rodando o frontend

    Navegue até a pasta frontend:

cd frontend

    Instale as dependências:

npm install

    Execute o frontend:

npm run dev

O frontend será servido em: http://localhost:5173
🔐 Usuário administrador padrão

O projeto cria automaticamente um usuário admin para acesso inicial:

    Usuário: seuUser

    Senha: suaSenha
