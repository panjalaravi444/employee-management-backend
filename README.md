# Employee Management System

A comprehensive Spring Boot application for managing employees and departments with JWT-based authentication.

## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Configuration](#configuration)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Default Credentials](#default-credentials)
- [API Usage Examples](#api-usage-examples)

## Overview

The Employee Management System is a RESTful API built with Spring Boot that provides comprehensive employee and department management capabilities. It includes secure JWT-based authentication, role-based access control, and robust data validation.

## Tech Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.1.5** - Application framework
- **Spring Security 6.1.5** - Security framework
- **Spring Data JPA** - Data access layer
- **Hibernate 6.2.13** - ORM framework
- **PostgreSQL** - Database
- **JWT (jjwt 0.12.3)** - Token-based authentication
- **Lombok** - Code generation
- **ModelMapper 3.1.1** - Object mapping
- **H2 Database** - In-memory database for console
- **Maven/Gradle** - Build tool

### Key Libraries
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- PostgreSQL Driver
- Jackson Datatype JSR310

## Features

- **Employee Management**
  - Create, read, update, delete employees
  - Search employees by name
  - Filter by department
  - Employee validation with custom constraints

- **Department Management**
  - Create, read, update, delete departments
  - View employees per department
  - Department validation

- **Authentication & Authorization**
  - JWT-based stateless authentication
  - Role-based access control (ADMIN, USER)
  - Secure password hashing with BCrypt
  - Token expiration handling

- **Data Validation**
  - Bean validation annotations
  - Custom validation messages
  - Input sanitization

- **Exception Handling**
  - Global exception handler
  - Custom error responses
  - Proper HTTP status codes

- **CORS Support**
  - Configured for frontend integration
  - Support for multiple origins

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/employeemanagement/
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java          # CORS configuration
│   │   │   │   └── DataInitializer.java     # Default data initialization
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java      # Authentication endpoints
│   │   │   │   ├── DepartmentController.java # Department CRUD
│   │   │   │   └── EmployeeController.java  # Employee CRUD
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java         # Standard API response
│   │   │   │   ├── AuthRequest.java         # Login request
│   │   │   │   ├── AuthResponse.java        # Login response
│   │   │   │   ├── DepartmentDTO.java       # Department data transfer
│   │   │   │   └── EmployeeDTO.java         # Employee data transfer
│   │   │   ├── entity/
│   │   │   │   ├── Department.java         # Department entity
│   │   │   │   ├── Employee.java           # Employee entity
│   │   │   │   └── User.java                # User entity for auth
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── ValidationException.java
│   │   │   ├── repository/
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   ├── EmployeeRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   ├── service/
│   │   │   │   ├── DepartmentService.java
│   │   │   │   └── EmployeeService.java
│   │   │   └── EmployeeManagementApplication.java
│   │   └── resources/
│   │       └── application.yml              # Application configuration
```

## Database Schema

### Departments Table
```sql
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Employees Table
```sql
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    position VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
```

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Login
```http
POST /v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin"
}
```

### Department Endpoints

#### Get All Departments
```http
GET /v1/departments
Authorization: Bearer <token>
```

#### Get Department by ID
```http
GET /v1/departments/{id}
Authorization: Bearer <token>
```

#### Create Department
```http
POST /v1/departments
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Engineering",
  "description": "Software development team"
}
```

#### Update Department
```http
PUT /v1/departments/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Engineering",
  "description": "Software development and IT"
}
```

#### Delete Department
```http
DELETE /v1/departments/{id}
Authorization: Bearer <token>
```

### Employee Endpoints

#### Get All Employees
```http
GET /v1/employees
Authorization: Bearer <token>
```

#### Get Employee by ID
```http
GET /v1/employees/{id}
Authorization: Bearer <token>
```

#### Search Employees
```http
GET /v1/employees/search?name=John
Authorization: Bearer <token>
```

#### Create Employee
```http
POST /v1/employees
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "dateOfBirth": "1990-01-01",
  "position": "Software Engineer",
  "departmentId": 1,
  "salary": 75000.00,
  "hireDate": "2023-01-15"
}
```

#### Update Employee
```http
PUT /v1/employees/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "dateOfBirth": "1990-01-01",
  "position": "Senior Software Engineer",
  "departmentId": 1,
  "salary": 85000.00,
  "hireDate": "2023-01-15"
}
```

#### Delete Employee
```http
DELETE /v1/employees/{id}
Authorization: Bearer <token>
```

## Authentication

### JWT Token Flow

1. **Login**: Client sends credentials to `/v1/auth/login`
2. **Token Generation**: Server validates credentials and generates JWT token
3. **Token Storage**: Client stores token (localStorage/sessionStorage)
4. **Authenticated Requests**: Client includes token in `Authorization: Bearer <token>` header
5. **Token Validation**: Server validates token on each request
6. **Token Expiration**: Token expires after 24 hours (configurable)

### Security Features

- **BCrypt Password Hashing**: Passwords are hashed using BCrypt
- **Stateless Authentication**: No server-side sessions
- **Token Expiration**: Tokens expire after 24 hours
- **Role-Based Access**: ADMIN and USER roles
- **CORS Protection**: Configured for specific origins
- **CSRF Disabled**: For stateless API architecture

## Configuration

### Application Configuration (application.yml)

```yaml
spring:
  application:
    name: Employee Management System
  datasource:
    url: jdbc:postgresql://localhost:5432/employee_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  jackson:
    serialization:
      write-dates-as-timestamps: false

server:
  port: 8080
  servlet:
    context-path: /api

app:
  jwt:
    secret: your-secret-key-change-this-in-production
    expiration: 86400000  # 24 hours in milliseconds
  cors:
    allowed-origins: http://localhost:3000,http://localhost:8080
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
```

### Environment Variables

For production, consider using environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION`

## Setup Instructions

### Prerequisites

- Java 21 or higher
- PostgreSQL 12 or higher
- Gradle or Maven
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Database Setup

1. **Install PostgreSQL**
   ```bash
   # Windows: Download from postgresql.org
   # Linux: sudo apt-get install postgresql
   # Mac: brew install postgresql
   ```

2. **Create Database**
   ```sql
   CREATE DATABASE employee_db;
   ```

3. **Update Credentials**
   Update `application.yml` with your PostgreSQL credentials:
   ```yaml
   spring:
     datasource:
       username: your_username
       password: your_password
   ```

### Build the Project

```bash
# Using Gradle
./gradlew clean build

# Using Maven
mvn clean install
```

## Running the Application

### From IDE
1. Open `EmployeeManagementApplication.java`
2. Run the main method

### From Command Line

```bash
# Using Gradle
./gradlew bootRun

# Using Maven
mvn spring-boot:run

# Using JAR
java -jar build/libs/employee-management-0.0.1-SNAPSHOT.jar
```

### Verify Application

- Application runs on: `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/api/h2-console`
- Health Check: `http://localhost:8080/api/actuator/health` (if actuator is enabled)

## Default Credentials

The application automatically creates default users on startup:

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | ADMIN, USER |
| user | user123 | USER |

**Important**: Change these passwords in production!

## API Usage Examples

### Using cURL

#### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### Get Employees
```bash
curl -X GET http://localhost:8080/api/v1/employees \
  -H "Authorization: Bearer <your-jwt-token>"
```

#### Create Employee
```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "dateOfBirth": "1990-01-01",
    "position": "Software Engineer",
    "departmentId": 1,
    "salary": 75000.00,
    "hireDate": "2023-01-15"
  }'
```

### Using Postman

1. **Login Request**
   - Method: POST
   - URL: `http://localhost:8080/api/v1/auth/login`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON):
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```

2. **Authenticated Request**
   - Method: GET
   - URL: `http://localhost:8080/api/v1/employees`
   - Headers: `Authorization: Bearer <token-from-login>`

## Error Handling

The API uses standard HTTP status codes:

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Invalid or missing token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### Error Response Format

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T12:00:00",
  "error": "VALIDATION_ERROR"
}
```

## Security Best Practices

### For Production

1. **Change JWT Secret**
   - Use a strong, randomly generated secret key
   - Store in environment variables, not in code

2. **Enable HTTPS**
   - Use SSL/TLS for all communications
   - Configure Spring Boot with SSL certificate

3. **Database Security**
   - Use strong database passwords
   - Restrict database access to application server only
   - Enable SSL for database connections

4. **CORS Configuration**
   - Restrict allowed origins to specific domains
   - Remove wildcard headers in production

5. **Logging**
   - Disable debug logging in production
   - Implement secure log aggregation
   - Don't log sensitive data (passwords, tokens)

6. **Dependency Updates**
   - Regularly update dependencies
   - Monitor for security vulnerabilities
   - Use OWASP Dependency Check

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure PostgreSQL is running
   - Check database credentials in `application.yml`

2. **Port Already in Use**
   - Change port in `application.yml` or stop conflicting service

3. **JWT Token Invalid**
   - Check token expiration
   - Verify JWT secret matches between requests

4. **CORS Errors**
   - Verify frontend origin is in allowed-origins
   - Check browser console for specific CORS errors

## Future Enhancements

- [ ] Refresh token implementation
- [ ] Password reset functionality
- [ ] Email verification
- [ ] File upload for employee photos
- [ ] Export data to CSV/PDF
- [ ] Audit logging
- [ ] Rate limiting
- [ ] API documentation with Swagger/OpenAPI
- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] CI/CD pipeline

## License

This project is for educational purposes.

## Contact

For questions or support, please contact the development team.
