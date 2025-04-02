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
- Advanced User List Features:
  - Pagination
  - Sorting
  - Filtering
  - Dynamic Querying

## Technology Stack

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT for stateless authentication
- Maven

## API Endpoints

### Authentication

- **POST /api/v1/auth/register** - Register a new user
- **POST /api/v1/auth/login** - Authenticate user and retrieve JWT token

### User Management

- **GET /api/v1/users** - Get all users with pagination, sorting, and filtering (Admin only)
  - Query Parameters:
    - Filtering:
      - `username` (optional) - Filter by username (case-insensitive partial match)
      - `email` (optional) - Filter by email (case-insensitive partial match)
      - `firstName` (optional) - Filter by first name (case-insensitive partial match)
      - `lastName` (optional) - Filter by last name (case-insensitive partial match)
      - `enabled` (optional) - Filter by enabled status
      - `emailVerified` (optional) - Filter by email verification status
    - Pagination:
      - `page` (default: 0) - Page number (0-based)
      - `size` (default: 10) - Number of items per page
    - Sorting:
      - `sortBy` (default: "id") - Field to sort by
      - `sortDirection` (default: "asc") - Sort direction ("asc" or "desc")
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

The application uses Spring Security with JWT for authentication. Each request to a protected endpoint must include an
Authorization header with a valid JWT token.

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

## PostgreSQL Database

The application uses PostgreSQL as the database. You can configure the database connection in
`src/main/resources/application.properties`.

- JDBC URL: `jdbc:postgresql://localhost:5432/user_management_db`
- Username: postgres
- Password: postgres

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

### Get all users with pagination and sorting

```http
GET /api/v1/users?page=0&size=10&sortBy=lastName&sortDirection=desc&username=john&enabled=true
Authorization: Bearer {jwt_token}
```

Response:
```json
{
    "content": [
        {
            "id": 1,
            "username": "john",
            "email": "john@example.com",
            "firstName": "John",
            "lastName": "Doe",
            "enabled": true,
            "roles": ["ROLE_USER"]
        }
        // ... more users
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "last": false,
    "first": true,
    "empty": false,
    "numberOfElements": 10,
    "hasNext": true,
    "hasPrevious": false,
    "nextPage": 1,
    "previousPage": -1
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
