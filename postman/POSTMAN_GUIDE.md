# Guide to Using the User Management API Postman Collection

This guide explains how to use the Postman collection for testing the User Management API.

## Importing the Collection

1. Open Postman
2. Click on "Import" button
3. Select the file `Clinicwave_API.postman_collection.json`
4. The collection will be imported with all pre-configured requests

## Testing Workflow

### Step 1: Start the Spring Boot Application

Make sure your Spring Boot application is running on `http://localhost:8080` before testing the API.

### Step 2: Authentication

The API uses JWT authentication. To test protected endpoints, you need to:

1. **Login or Register first to get a JWT token**:
   - Use "Login as Admin" request with default credentials (username: `admin`, password: `admin123`)
   - Or register a new user with "Register User" request
   - Or register a new admin with "Register Admin" request

2. **Set the JWT token in the Postman environment variable**:
   - After successful login, you'll receive a response containing the JWT token
   - Copy the token value (without the "Bearer " prefix)
   - In Postman, click on the "Environment quick look" button (the eye icon in the top right)
   - Set the appropriate variable (`userToken`, `adminToken`, or `modToken`) with the token value
   - The requests are pre-configured to use these variables in the Authorization header

### Step 3: Test the Endpoints

#### Test Endpoints
- **Public Content**: Accessible to anyone
- **User Content**: Accessible to users with ROLE_USER
- **Moderator Content**: Accessible to users with ROLE_MODERATOR
- **Admin Content**: Accessible to users with ROLE_ADMIN

#### Authentication Endpoints
- **Register User**: Creates a new user with ROLE_USER and sends a verification email
- **Register Admin**: Creates a new user with ROLE_ADMIN and sends a verification email
- **Login as User**: Authenticates a regular user and returns a JWT token
- **Login as Admin**: Authenticates an admin user and returns a JWT token
- **Refresh Token**: Refreshes the JWT token using a refresh token
- **Logout**: Invalidates the JWT token

#### User Management Endpoints
- **Get All Users**: Admin only - retrieves all users
- **Get User by ID**: Admin or same user - retrieves user by ID
- **Get Current User**: Any authenticated user - retrieves the current user's profile
- **Update User**: Admin or same user - updates user information
- **Update User Roles**: Admin only - updates a user's roles
- **Change Password**: Admin or same user - changes user password
- **Delete User**: Admin only - deletes a user

#### Email Verification Endpoints
- **Verify Email**: Verifies the user's email address
- **Resend Verification Email**: Resends the verification email to the user

## Tips for Testing

1. Always start with authentication to get a valid JWT token
2. Store the token in the environment variables
3. User ID values in URLs (like `/users/1`) might need to be adjusted based on your database
4. The default admin user is created automatically on startup
5. Test the role-based authorization by using different tokens (user, moderator, admin)
6. Check error responses when testing validation or authorization
