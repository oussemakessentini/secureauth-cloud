# SecureAuth Cloud

Enterprise-grade authentication and authorization platform built with Spring Boot, React, PostgreSQL, JWT, Docker, and AWS-ready architecture.

SecureAuth Cloud demonstrates real-world backend and frontend engineering practices including JWT authentication, refresh token management, email verification, password reset flows, role-based access control (RBAC), admin management, protected frontend routing, and production-ready deployment preparation.

---

# Features

## Authentication & Security

- User Registration
- User Login
- JWT Access Tokens
- Refresh Token Authentication
- Automatic Access Token Refresh
- Email Verification
- Forgot Password
- Reset Password
- Change Password
- Logout Functionality
- BCrypt Password Encryption
- Spring Security Integration

---

## Authorization

- Role-Based Access Control (RBAC)
- USER Role
- ADMIN Role
- Protected API Endpoints
- Protected Frontend Routes
- Admin-Only Pages

---

## Admin Features

- View All Users
- Enable / Disable Users
- Promote Users to Admin
- Remove Admin Role
- User Status Management

---

## Frontend Features

- React + Vite Frontend
- Protected Routes
- Admin Routes
- Axios JWT Interceptors
- Automatic Token Refresh
- Responsive Bootstrap UI
- Email Verification Page
- Password Reset Page

---

## DevOps & Cloud

- Docker Support
- Docker Compose
- Environment Variables
- AWS-Ready Architecture
- PostgreSQL Integration

---

# Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- JWT (jjwt)
- Maven
- Lombok

---

## Frontend

- React
- Vite
- Axios
- React Router DOM
- Bootstrap

---

## DevOps / Cloud

- Docker
- Docker Compose
- AWS (planned deployment)

---

# Architecture

```text
Frontend (React + Vite)
        |
        v
Spring Boot REST API
        |
        v
Spring Security + JWT
        |
        v
PostgreSQL Database
```

---

# Main Functionalities

## User Registration Flow

1. User registers
2. Verification token generated
3. Verification email sent
4. User clicks verification link
5. Account activated
6. User can login

---

## Authentication Flow

1. User logs in
2. Backend generates:
   - Access Token
   - Refresh Token
3. Frontend stores tokens
4. Protected APIs require JWT
5. Expired access tokens refresh automatically

---

## Password Reset Flow

1. User requests password reset
2. Reset token generated
3. Email sent to user
4. User opens reset link
5. Password updated securely

---

# Project Structure

```text
secureauth-cloud/
│
├── backend/
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── application.properties
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
│
├── docker-compose.yml
│
└── README.md
```

---

# Backend Setup

## Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/secureauth-cloud.git
```

---

## Navigate to Backend

```bash
cd backend
```

---

## Configure Environment Variables

Create environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5433/secureauth_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=your-super-secret-key-minimum-32-characters

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

ADMIN_EMAIL=admin@secureauth.com
ADMIN_PASSWORD=Admin12345
```

---

## Run PostgreSQL with Docker

From project root:

```bash
docker compose up -d
```

---

## Run Backend

```bash
mvn clean install
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

---

# Frontend Setup

## Navigate to Frontend

```bash
cd frontend
```

---

## Install Dependencies

```bash
npm install
```

---

## Run Frontend

```bash
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

# Swagger Documentation

Open Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Main API Endpoints

## Authentication

```text
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh-token
GET    /api/auth/me
POST   /api/auth/change-password
```

---

## Email Verification

```text
GET    /api/auth/verify-email/{token}
POST   /api/auth/resend-verification
```

---

## Password Reset

```text
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
```

---

## Admin Management

```text
GET     /api/admin/users
PATCH   /api/admin/users/{userId}/status
PATCH   /api/admin/users/{userId}/roles/add
PATCH   /api/admin/users/{userId}/roles/remove
```

---

# Security Features

- JWT Authentication
- Refresh Token Rotation
- BCrypt Password Hashing
- Role-Based Authorization
- Protected API Routes
- Protected Frontend Routes
- Email Verification
- Password Reset Tokens
- Admin Authorization
- CORS Configuration

---

# Screenshots

## Login Page

![Login Page](screenshots/login.png)

---

## Register Page

![Register Page](screenshots/register.png)

---

## Forgot Password Page

![Forgot Password Page](screenshots/forgot-password.png)

## Dashboard

![Dashboard](screenshots/dashboard.png)

---

## Admin User Management

![Admin Users](screenshots/admin-users.png)

---

# Docker Support

## Start Database

```bash
docker compose up -d
```

---

## Build Backend Docker Image

```bash
docker build -t secureauth-api .
```

---

# Future Improvements

- AWS Deployment
- CI/CD Pipeline
- Kubernetes Deployment
- Redis Caching
- OAuth2 Login
- Google Authentication
- Rate Limiting
- Multi-Factor Authentication (MFA)
- Audit Logs
- User Activity Tracking
- Email Templates
- Mobile App Support

---

# AWS Deployment Plan

Planned AWS services:

- EC2
- RDS PostgreSQL
- S3
- CloudFront
- Route53
- AWS SES
- Docker Deployment
- GitHub Actions CI/CD

---

# Author

Oussama Ksantini

Software Engineer | Full Stack Developer | Cloud Enthusiast

---

# License

This project is for educational and portfolio purposes.
