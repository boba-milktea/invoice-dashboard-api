# Invoice Dashboard API

REST backend for managing **users**, **clients**, and **invoices** with line items, backed by PostgreSQL and secured with **JWT** authentication. Built as a portfolio-ready Spring Boot service.

**Repository:** [github.com/boba-milktea/invoice-dashboard-java](https://github.com/boba-milktea/invoice-dashboard-java)

---

## Tech stack

| Layer | Technology |
|--------|------------|
| Runtime | Java **21** |
| Framework | Spring Boot **4.0.6** (Web MVC, Data JPA, Security, Validation) |
| Database | **PostgreSQL** |
| Security | Spring Security, **BCrypt** passwords, **JJWT** |
| Mapping | **MapStruct**, Lombok |
| API docs | **springdoc-openapi** (optional; disabled by default) |

---

## Features

### Authentication

- `POST /api/v1/auth/register` — create an account (password hashed with BCrypt).
- `POST /api/v1/auth/login` — returns a JWT for subsequent requests.

### Users

- `GET /api/v1/users/me`, `PATCH /api/v1/users/me` — current user profile.
- `GET /api/v1/users`, `GET /api/v1/users/{id}`, `PATCH /api/v1/users/{id}`, `PATCH /api/v1/users/{id}/role`, `DELETE /api/v1/users/{id}` — user administration.

**Note:** `@PreAuthorize("hasRole('ADMIN')")` on admin-style user routes is **commented out** in code. The **HTTP security rules** still require a valid JWT for these paths; fine-grained admin-only enforcement on user endpoints is the intended next step once those annotations are enabled.

### Clients

- Full CRUD under `/api/v1/clients`.
- `GET /api/v1/clients/search?name=...` — filter clients by associated username.
- Creating a client accepts a `userId` in the body so the record is linked to the owning user.

### Invoices

- `GET /api/v1/invoices` — paginated list for the authenticated user (default: 10 per page, sorted by `createdAt` descending).
- `GET /api/v1/invoices/due` — invoices due attention (`OVERDUE`, `PENDING`).
- `GET /api/v1/invoices/search/by-amount?min=&max=` — filter by total amount range.
- `GET /api/v1/invoices/{reference}`, `POST`, `PATCH`, `DELETE` — manage invoices by **reference** (per-user uniqueness).
- Line items: `POST /api/v1/invoices/{reference}/items`, `PATCH .../items/{itemId}`, `DELETE .../items/{itemId}`.

**Business rules:**

- Invoice reference is unique **per user** (`user_id` + `invoice_ref`).
- Due date must be on or after the issue date.
- **Subtotal**, **tax**, and **total** are derived from line items; tax uses a **21%** VAT rate on the subtotal.

### Cross-cutting

- Global exception handling for validation, conflicts, and common errors (`GlobalExceptionHandler`).
- Request logging via `LoggingFilter` in the security filter chain.

---

## Security model

Rules are defined in `SecurityConfig`:

| Area | Access |
|------|--------|
| `/api/v1/auth/**` | Public (no JWT) |
| `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-ui.html` | Public (useful when OpenAPI is enabled) |
| `/api/v1/clients/**`, `/api/v1/invoices/**` | **`ROLE_ADMIN` only** |
| All other routes (e.g. `/api/v1/users/**`) | Any **authenticated** user (valid JWT) |

Registration assigns **`Role.USER`** by default. To call **client** or **invoice** APIs you need an account with **`ROLE_ADMIN`** (for example, promote a user via `PATCH /api/v1/users/{id}/role` while authenticated, or set the role directly in the database for local development).

---

## Database model

### Tables and relationships

| JPA entity | Table | Notes |
|------------|--------|--------|
| `User` | `users` | UUID primary key; 1:N to clients and invoices |
| `Client` | `client` | UUID PK; `user_id` → `users`; 1:N invoices |
| `Invoice` | `invoice` | Long PK (sequence); `user_id`, `client_id`; **unique** `(user_id, invoice_ref)`; 1:N line items |
| `InvoiceItem` | `invoice_item` | Long PK (sequence); `invoice_id` → `invoice` |

### Enums

- **Invoice `Status`:** `DRAFT`, `PENDING`, `PAID`, `OVERDUE`, `CANCELLED`
- **User `Role`:** `ADMIN`, `USER`

### ER diagram

```mermaid
erDiagram
    User {
        uuid id PK
        string name
        string email UK
        string password
        string role
        datetime createdAt
        datetime updatedAt
    }
    Client {
        uuid id PK
        string name
        string email UK
        string address
        uuid user_id FK
        datetime createdAt
        datetime updatedAt
    }
    Invoice {
        long id PK
        string invoice_ref
        date issue_date
        date due_date
        string status
        decimal subtotal
        decimal taxAmount
        decimal totalAmount
        uuid user_id FK
        uuid client_id FK
        datetime createdAt
        datetime updatedAt
    }
    InvoiceItem {
        long id PK
        string description
        int quantity
        decimal unitPrice
        decimal lineTotal
        long invoice_id FK
        datetime createdAt
        datetime updatedAt
    }
    User ||--o{ Client : owns
    User ||--o{ Invoice : owns
    Client ||--o{ Invoice : billedTo
    Invoice ||--o{ InvoiceItem : lines
```

Hibernate `ddl-auto` is set to `update` for development (see `application.properties`).

---

## Project structure

```
src/main/java/edu/hyf/invoice/
├── InvoiceApplication.java          # Entry point
├── auth/                            # Register, login, logging filter
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── LoggingFilter.java
│   └── dto/
├── config/
│   └── SecurityConfig.java
├── user/                            # User entity, Role, CRUD + /me
├── client/                          # Client CRUD, mapper, repository
├── invoice/                         # Invoice + InvoiceItem, services, Status
├── security/                        # JWT filter, JwtService, UserPrincipal, user details
└── common/exception/                # Domain exceptions, GlobalExceptionHandler

src/main/resources/
└── application.properties           # Datasource, JPA, server, springdoc flags

application.yml                      # JWT secret and expiration (JWT_SECRET)
src/test/java/                       # Spring Boot tests
```

---

## Getting started

### Prerequisites

- **JDK 21**
- **PostgreSQL**
- **Maven** (or use the included Maven Wrapper `./mvnw`)

### Configuration

1. Create a PostgreSQL database for the app.

2. Add **`env.properties`** at the **project root** (Spring imports `file:env.properties` from `application.properties`):

   ```properties
   DB_DATABASE=your_database_name
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

3. Set **`JWT_SECRET`** in your environment (used by `application.yml` under `app.jwt.secret`). Use a long, random value in production.

4. Optional: adjust JWT lifetime via `app.jwt.expiration-ms` in `application.yml` (default is 86400000 ms, i.e. 24 hours).

### Run

```bash
./mvnw spring-boot:run
```

The API listens on **port 8080** by default (`server.port` in `application.properties`).

Authenticated requests should send the JWT in the **`Authorization`** header as `Bearer <token>` (see `JwtAuthFilter`).

---

## API documentation (Swagger)

OpenAPI generation and Swagger UI are **turned off** by default:

```properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

To try interactive docs locally, set both to **`true`** in `src/main/resources/application.properties`, restart the app, then open the Swagger UI path (typically `http://localhost:8080/swagger-ui.html` or the redirect under `/swagger-ui/` for your springdoc version). `SecurityConfig` already permits those paths without authentication.

---

## Author

**Catherine** — [catherine.idv@gmail.com](mailto:catherine.idv@gmail.com)

License: **MIT** (see `pom.xml`).
