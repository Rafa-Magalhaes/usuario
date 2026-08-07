# 👤 API Usuário (Core Relacional)

## 📌 Visão Geral
Microsserviço responsável pelo gerenciamento de identidades, autenticação, controle de perfis e persistência relacional de usuários, endereços e telefones. Implementa a barreira de segurança primária e o ecossistema de geração de Tokens JWT.

## 🛠️ Stack Tecnológico
* **Java 21** | **Spring Boot 3.4.x**
* **Spring Security & JWT** (JJWT 0.12.6)
* **Spring Data JPA & PostgreSQL**
* **Springdoc OpenAPI (Swagger UI)**

## 🚀 Como Executar Localmente
1. Certifique-se de ter o PostgreSQL rodando na porta `5432`.
2. Configure o arquivo `src/main/resources/application-dev.yml` com suas credenciais locais do banco.
3. Execute a aplicação através da sua IDE (IntelliJ) ou via terminal:
   ```bash
   ./gradlew bootRun
   ```

## 🔌 Documentação (Swagger)
Com a aplicação rodando na porta `8084`, acesse a documentação interativa:
🔗 [Swagger UI - API Usuário](http://localhost:8084/swagger-ui/index.html)