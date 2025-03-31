# User Management Service

A complete Spring Boot REST API for user management with JWT authentication and role-based authorization.

## Features

- User Registration and Login
- JWT Authentication
- Role-Based Authorization
- User Profile Management
- Password Management
- Exception Handling
- Data Validation

## Technology Stack

- Java 17
- Spring Boot 3.2.4
- Spring Security
- Spring Data JPA
- H2 Database (for development)
- MySQL support
- JWT for stateless authentication
- Maven

## API Endpoints

### Authentication

- **POST /api/v1/auth/register** - Register a new user
- **POST /api/v1/auth/login** - Authenticate user and retrieve JWT token

### User Management

- **GET /api/v1/users** - Get all users (Admin only)
- **GET /api/v1/users/{id}** - Get user by ID (Admin or same user)
- **GET /api/v1/users/me** - Get current user profile
- **PUT /api/v1/users/{id}** - Update user (Admin or same user)
- **DELETE /api/v1/users/{id}** - Delete user (Admin only)
- **POST /api/v1/users/{id}/change-password** - Change user password (Admin or same user)

### Test Endpoints

- **GET /api/v1/test/all** - Public access
- **GET /api/v1/test/user** - User role access
- **GET /api/v1/test/mod** - Moderator role access
- **GET /api/v1/test/admin** - Admin role access

## Security

The application uses Spring Security with JWT for authentication. Each request to a protected endpoint must include an Authorization header with a valid JWT token.

Example:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Security Documentation

- [Security Implementation](./docs/SECURITY.md) - Overview of the security architecture
- [Security Flow](./docs/SECURITY_FLOW.md) - Detailed request flow through security components

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:
   ```
   mvn spring-boot:run
   ```

## Default Admin User

A default admin user is created on startup:
- Username: admin
- Password: admin123

## H2 Database Console

The H2 database console is available at `/api/v1/h2-console` with the following credentials:
- JDBC URL: jdbc:h2:mem:userdb
- Username: sa
- Password: password

## API Request Examples

### Register a new user

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "user1",
  "email": "user1@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["ROLE_USER"]
}
```

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "password123"
}
```

### Update user

```http
PUT /api/v1/users/1
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

### Change password

```http
POST /api/v1/users/1/change-password
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentPassword": "password123",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

### Postman Documentation

- [Postman Guide](./postman/POSTMAN_GUIDE.md) - Contains instructions on how to import and use the Postman collection for testing API endpoints.
- [Postman Collection](./postman/Clinicwave_API.postman_collection.json) - A Postman collection with pre-configured API requests for easy testing of the User Management Service.