<div align="center">

# 🏦 Banking Application System

### A secure, production-ready RESTful Banking Backend

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Secured-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

</div>

---

## 📌 Overview

The **Banking Application System** is a fully-functional backend REST API that simulates core banking operations. Built with **Spring Boot 3.x** and **Java 21**, it demonstrates clean architecture, JWT-secured endpoints, transactional fund management, and production-grade exception handling — all backed by a **PostgreSQL** relational database.

This project showcases backend engineering skills directly relevant to fintech and enterprise application development.

---

## ✨ Features

| Feature | Description |
|---|---|
| **Account Management** | Create, retrieve, and delete bank accounts |
| **Deposits & Withdrawals** | Credit and debit operations with balance checks |
| **Fund Transfers** | Atomic transfers between two accounts via REST |
| **Transaction History** | Full audit trail of all account transactions |
| **JWT Authentication** | Stateless, token-based security for all endpoints |
| **Global Exception Handling** | Consistent error responses using `@ControllerAdvice` |
| **DTO Pattern** | Clean separation between API contracts and domain models via ModelMapper |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.0.1 |
| Security | Spring Security + JWT (jjwt 0.13.0) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build Tool | Maven |
| Utilities | Lombok, ModelMapper 3.1.1 |

---

## 📁 Project Structure

```
Banking-Application-System/
├── src/
│   └── main/
│       ├── java/com/Bank/app/
│       │   ├── controller/       # REST controllers (Account, Transaction)
│       │   ├── service/          # Business logic layer
│       │   ├── repository/       # JPA repositories
│       │   ├── entity/           # JPA entities (Account, Transaction)
│       │   ├── dto/              # Data Transfer Objects
│       │   ├── security/         # JWT filter, UserDetails, SecurityConfig
│       │   └── exception/        # Custom exceptions & global handler
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```

---

## 🔌 API Endpoints

### Account Operations

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/accounts` | Create a new bank account |
| `GET` | `/api/accounts/{id}` | Get account details by ID |
| `GET` | `/api/accounts` | List all accounts |
| `DELETE` | `/api/accounts/{id}` | Delete an account |

### Transaction Operations

| Method | Endpoint | Description |
|---|---|---|
| `PUT` | `/api/accounts/{id}/deposit` | Deposit amount to account |
| `PUT` | `/api/accounts/{id}/withdraw` | Withdraw amount from account |
| `POST` | `/api/accounts/transfer` | Transfer funds between accounts |
| `GET` | `/api/accounts/{id}/transactions` | Get full transaction history |

### Auth

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Authenticate and receive JWT token |

> All endpoints (except `/api/auth/**`) require a valid JWT token in the `Authorization: Bearer <token>` header.

---

## ⚙️ Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone the Repository

```bash
git clone https://github.com/CGaganGowda/Banking-Application-System.git
cd Banking-Application-System
```

### 2. Configure the Database

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bankdb
spring.datasource.username=postgres
spring.datasource.password=9999
spring.jpa.hibernate.ddl-auto=update

app.jwt.secret=your_jwt_secret_key
app.jwt.expiration=86400000
```

### 3. Build & Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`

---

## 🔐 Security

- All APIs are secured with **JWT (JSON Web Token)** authentication
- Passwords are encrypted using **BCrypt**
- `AccountNotFoundException` and `InsufficientFundsException` return structured error bodies
- A global `@ControllerAdvice` ensures consistent error handling across all endpoints

---

## 🗺️ Roadmap

- [ ] Add pagination to transaction history
- [ ] Implement account types (Savings, Current)
- [ ] Add Swagger / OpenAPI documentation
- [ ] Write unit and integration tests
- [ ] Dockerize the application

---

## 👤 Author

**Gagan Gowda**
[![GitHub](https://img.shields.io/badge/GitHub-CGaganGowda-181717?style=flat-square&logo=github)](https://github.com/CGaganGowda)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin)](https://www.linkedin.com/in/gagan-c-gowda/)

---

<div align="center">
⭐ If you find this project useful, consider giving it a star!
</div>
