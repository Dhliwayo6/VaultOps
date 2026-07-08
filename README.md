# VaultOps: Secure Enterprise Asset Lifecycle Management

VaultOps is a secure, high-performance, and feature-rich Asset Lifecycle and Inventory Management platform designed to track, monitor, and audit corporate hardware assets. The application is built as a monorepo split into a containerized Spring Boot 3.x REST API backend and a responsive React client frontend.

---

## Table of Contents
- [Core Features](#core-features)
- [Architecture & Design Decisions](#architecture--design-decisions)
- [Technology Stack](#technology-stack)
- [Project Directory Structure](#project-directory-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Backend Setup & Run](#backend-setup--run)
  - [Frontend Setup & Run](#frontend-setup--run)
  - [Docker Compose Deployment](#docker-compose-deployment)
- [Security & Hardening](#security--hardening)
  - [Authentication Lifecycle](#authentication-lifecycle)
  - [Network & Database Security](#network--database-security)
  - [OWASP Security Headers](#owasp-security-headers)
  - [Incident Response & Threat Hunting](#incident-response--threat-hunting)
- [Testing & Quality Gates](#testing--quality-gates)

---

## Core Features

### Frontend Client
- **Interactive Analytics Dashboard**: Real-time telemetry, quick-stat panels (active assets in storage, in repairs, and in use), and repair duration metrics.
- **Unified Asset Registry**: Interactive grid view featuring paginated queries, client/server search, column-level sorting, and status/condition indicators.
- **Adaptive Theme Engine**: Smooth dark/light mode toggling with system preference detection, persistent local storage, and inline anti-flash styling.
- **Premium CSS Motion Language**: Subtle cubic-bezier hover transitions, responsive sidebar, and smooth dynamic layouts conforming to WCAG AA accessibility standards.
- **Reduced Motion Support**: Automated overrides using CSS media queries (`prefers-reduced-motion: reduce`) for accessibility-compliant system configurations.

### Backend API Services
- **Stateless Session Hardening**: Strict JWT session authentication utilizing Access Tokens (short-lived) and Refresh Tokens (rotated with reuse-detection mechanisms).
- **Asynchronous CSV & Excel Import**: Background processing of spreadsheet assets powered by Spring `@Async` task executors, complete with validation reports and execution history.
- **Streaming Large Exports**: Efficient spreadsheet downloads using Apache POI's streaming API (`SXSSFWorkbook`) to write records in chunks and prevent JVM heap exhaustion.
- **Brevo SMTP Mail Integration**: Automatic delivery of transactional security emails (OTP verification codes, password reset links).
- **Correlation ID Tracking**: Request lifecycle tracing using custom filters and MDC mapping, outputting a uniform `X-Correlation-ID` header.
- **Abuse Prevention & Rate Limiting**: Bulletproof request throttling and login lockout (max 5 failed login attempts per account/IP within 15 minutes).

---

## Architecture & Design Decisions

- **Monorepo Design**: Consolidated frontend and backend projects under a single workspace.
- **Backend Service Consolidation**: Extracted over-engineered CQRS logic into unified service classes (`AssetService` and `StatsService`) to eliminate class explosion and simplify CRUD operations.
- **Flyway Database Migration**: Database schemas, enum mapping, and initial seed data are managed dynamically using Flyway migration scripts.
- **Insecure Direct Object Reference (IDOR) Protection**: Strict user-scoped resource ownership verification on endpoints (e.g. `ImportLog` and profile details). Unauthorized requests result in a silent `404 Not Found` response to hide the existence of resources.

---

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.x (Java 17)
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **Database Access**: Spring Data JPA, Hibernate
- **Migrations**: Flyway Migration
- **Bulk Data Handling**: Apache POI (SXSSF Streaming Excel)
- **Mailing**: Brevo SMTP Integration
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18, Vite (ESLint configuration)
- **Styling**: Tailwind CSS, Vanilla CSS Custom Properties
- **Routing**: React Router (Route-based Code Splitting)
- **Tests**: Vitest, React Testing Library, JSDOM

### Infrastructure & Database
- **Database**: MySQL 8.x (Production/Local), H2 (In-memory testing)
- **Containerization**: Docker, Docker Compose

---

## Project Directory Structure

```text
VaultOps/
├── backend/vaultops/       # Spring Boot Backend API
│   ├── src/main/java/      # Backend Java Source Code
│   ├── src/main/resources/ # Configurations & Flyway migrations
│   ├── Dockerfile          # Multi-stage production container build
│   └── docker-compose.yml  # Local developer infrastructure (MySQL + App)
├── frontend/               # React Vite Frontend Client
│   ├── src/app/            # App routes and shell layouts
│   ├── src/features/       # Feature-centric modules (Auth, Assets, Dashboard)
│   ├── src/components/     # Reusable UI controls and indicators
│   ├── src/styles/         # Global styles & design token declarations
│   ├── vitest.config.js    # Vitest runner configuration
│   └── package.json        # Frontend dependency configurations
├── docs/                   # Extended developer guidelines & future roadmaps
└── git-hooks/              # Custom Git pre-commit security verification scripts
```

---

## Getting Started

### Prerequisites
Ensure the following tools are installed on your workstation:
- **Java Development Kit (JDK) 17+**
- **Node.js 18+** (with npm)
- **Docker & Docker Compose** (optional, recommended for DB setup)
- **MySQL 8.x** (if running a bare-metal database)

---

### Backend Setup & Run

1. Navigate to the backend directory:
   ```bash
   cd backend/vaultops
   ```

2. Create a local `.env` configuration file from the template:
   ```bash
   cp .env.example .env
   ```

3. Open `.env` and configure your credentials:
   ```env
   SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/vaultops
   SPRING_DATASOURCE_USERNAME=your_db_user
   SPRING_DATASOURCE_PASSWORD=your_db_password
   BREVO_SMTP_USERNAME=your_brevo_username
   BREVO_SMTP_KEY=your_brevo_key
   MAIL_SENDER_ADDRESS=noreply@vaultops.internal
   JWT_SECRET_KEY=highly_secure_256_bit_random_signing_key_here
   ```

4. Compile and launch the Spring Boot API:
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend API service will bind to port `8081` by default.

---

### Frontend Setup & Run

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Configure environmental endpoints:
   Create a `.env` file (or use `.env.example` as reference):
   ```env
   VITE_API_URL=http://localhost:8081
   ```

4. Start the Vite hot-reloading development server:
   ```bash
   npm run dev
   ```
   Open the browser client at `http://localhost:5173`.

---

### Docker Compose Deployment
To deploy the database and backend application using Docker containers:

1. Build and run the stack:
   ```bash
   cd backend/vaultops
   docker-compose up --build -d
   ```
2. Verify service containers are running and healthy:
   ```bash
   docker ps
   ```

---

## Security & Hardening

### Authentication Lifecycle
- **Access Tokens**: Issued as short-lived JWTs. Validated stateless on each API request.
- **Refresh Token Rotation (RTR)**: Long-lived refresh tokens are stored in secure, `HttpOnly`, `SameSite=Lax`, and `Secure` cookies. A new refresh token is issued on each rotation.
- **Token Reuse Detection**: If a revoked refresh token is re-submitted, the system assumes a security compromise, immediately invalidates all active tokens for that user, and forces a logout across all devices.
- **Logout Denylisting**: Access tokens are hashed using SHA-256 and blacklisted in the database upon logout, preventing token reuse until their natural expiration.

### Network & Database Security
- **Localhost Port Binding**: Database port exposure is restricted to the loopback interface (`127.0.0.1:3307:3306`) in `docker-compose.yml` to prevent public network access.
- **Least-Privilege Roles**: Custom SQL scripts assign restricted permissions to the application's MySQL user account (`SELECT, INSERT, UPDATE, DELETE` only inside `vaultops`).

### OWASP Security Headers
The API is configured with strict security response headers:
- `Strict-Transport-Security: max-age=31536000; includeSubDomains` (Force HTTPS)
- `X-Frame-Options: DENY` (Prevent Clickjacking)
- `Content-Security-Policy: default-src 'self'` (Restrict script execution context)
- `X-Content-Type-Options: nosniff` (Prevent MIME-type sniffing)
- `Referrer-Policy: no-referrer` (Hide HTTP Referrers)

### Incident Response & Threat Hunting
To investigate security events, run these commands directly on the host server logs (`vaultops.log`):

* **Brute-Force Attack Detection**:
  ```bash
  grep "Authentication failure" /path/to/vaultops.log | awk -F' - ' '{print $2}' | sort | uniq -c | sort -nr | head -n 20
  ```

* **Rate Limit (Too Many Requests) Tracking**:
  ```bash
  grep -E "Rate limit exceeded|Login blocked" /path/to/vaultops.log | grep -oE "IP: [0-9.]+|IP [0-9.]+" | awk '{print $NF}' | sort | uniq -c | sort -nr
  ```

* **Correlation Trace**:
  ```bash
  grep "CorrelationID: <CORRELATION_ID>" /path/to/vaultops.log
  ```

---

## Testing & Quality Gates

### Backend Tests
- Powered by JUnit 5, AssertJ, and Mockito.
- Integrates Flyway migrations on an H2 in-memory database to match production schemas.
- **Quality Gate**: JaCoCo enforces a **minimum 60% line coverage** on the service package (`com.vaultops.services`).
- Run the suite:
  ```bash
  cd backend/vaultops
  ./mvnw clean verify
  ```

### Frontend Tests
- Powered by Vitest, React Testing Library, and `@vitest/coverage-v8`.
- Gated using custom Git pre-commit hooks executing test runs and ESLint validations before permitting commits.
- Run the suite:
  ```bash
  cd frontend
  npm run test
  ```
- Generate a coverage report:
  ```bash
  npm run test:coverage
  ```

---

