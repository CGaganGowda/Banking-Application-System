# Banking Application System

A production-style backend REST API for core banking operations — built with Spring Boot 4, Spring Security, JWT authentication, and PostgreSQL. Supports account management, fund transfers, and full transaction history.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?style=flat&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat)

---

## Features

- Create, retrieve, update, and delete bank accounts
- Deposit and withdraw funds with balance validation
- Fund transfers between two accounts (atomic)
- Full transaction history per account
- JWT-based authentication with Spring Security
- Global exception handling with structured error responses
- DTO mapping via ModelMapper

---

## Tech stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.1 |
| Language | Java 21 |
| Security | Spring Security + jjwt 0.13.0 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Mapping | ModelMapper 3.1.1 |
| Boilerplate | Lombok 1.18.38 |
| Build | Maven |

---

## Architecture

Follows a standard 3-layer Spring Boot architecture:

```
HTTP Request → Controller → Service → Repository → PostgreSQL
                   ↑
            JWT Security Filter
```

- **Controller** — handles HTTP requests, validates input, delegates to service
- **Service** — business logic, DTO ↔ entity mapping via ModelMapper
- **Repository** — Spring Data JPA interfaces for DB access
- **Security** — JWT filter chain sits in front of all protected endpoints
- **Exception handling** — global `@ControllerAdvice` returns structured JSON errors

---

## API endpoints

### Account endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/accounts` | Create a new account | Required |
| `GET` | `/api/accounts/{id}` | Get account by ID | Required |
| `GET` | `/api/accounts` | List all accounts | Required |
| `PUT` | `/api/accounts/{id}/deposit` | Deposit amount | Required |
| `PUT` | `/api/accounts/{id}/withdraw` | Withdraw amount | Required |
| `DELETE` | `/api/accounts/{id}` | Delete account | Required |

### Transfer endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/accounts/transfer` | Transfer funds between accounts | Required |

### Transaction endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/accounts/{id}/transactions` | Get all transactions for an account | Required |

### Auth endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user | Public |
| `POST` | `/api/auth/login` | Login and receive JWT token | Public |

---

## Local setup

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone the repository

```bash
git clone https://github.com/CGaganGowda/Banking-Application-System.git
cd Banking-Application-System
```

### 2. Create the database

```sql
psql -U postgres
CREATE DATABASE banking_db;
```

### 3. Configure application.properties

In `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
app.jwt.secret=your_jwt_secret_key_here
app.jwt.expiration=86400000
```

### 4. Build and run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### 5. Test with Postman

1. Register: `POST /api/auth/register` with `{ "username": "...", "password": "..." }`
2. Login: `POST /api/auth/login` — copy the JWT token from the response
3. Add `Authorization: Bearer ` header to all subsequent requests

---

## Exception handling

All errors return a consistent JSON response:

```json
{
  "status": 404,
  "error": "Account Not Found",
  "message": "Account with id 5 does not exist",
  "timestamp": "2024-01-15T10:30:00"
}
```

Custom exceptions: `AccountNotFoundException`, `InsufficientBalanceException`, handled by a global `@ControllerAdvice`.

---

## Project structure

```
src/main/java/com/Bank/app/
├── controller/       # REST controllers
├── service/          # Business logic interfaces + implementations
├── repository/       # Spring Data JPA repositories
├── entity/           # JPA entities (Account, Transaction)
├── dto/              # Request/Response DTOs
├── security/         # JWT filter, UserDetailsService, SecurityConfig
└── exception/        # Custom exceptions + GlobalExceptionHandler
```

---

## Author

**Gagan Gowda** — [GitHub](https://github.com/CGaganGowda) · [LinkedIn](https://linkedin.com/in/gagan-c-gowda)
