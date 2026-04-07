# Monio API

Backend service for Monio, a full stack application built with Java and Spring Boot.

## Overview

Monio API provides the backend for the Monio platform, handling user authentication, session management, business logic, and database access. The project is structured using a layered architecture to keep responsibilities clear and the codebase maintainable.

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- SQL Database
- Flyway
- Maven

## Features

- User registration and authentication
- JWT-based authentication and session handling
- Layered architecture with controllers, services, DTOs, entities, and repositories
- Database access using Spring Data JPA
- Version-controlled schema migrations with Flyway
- RESTful API design
- Separation of concerns for easier maintenance and scalability

## Project Structure

- `controller` – API endpoints and request handling
- `service` – business logic
- `dto` – request and response data transfer objects
- `entity` – persistence models
- `repository` – database access with JPA
- `config/security` – authentication and JWT configuration
- `migration` – Flyway database migrations

## Architecture Notes

The backend follows a typical Spring Boot layered approach:

- Controllers handle HTTP requests and responses
- Services contain business logic
- DTOs are used to separate API contracts from persistence models
- Repositories manage database interaction
- JWT is used for secure authentication and protected routes
- Flyway manages database schema changes consistently across environments

## Running the Project

### Prerequisites

- Java 17+  
- Maven  
- SQL database configured locally or remotely

### Setup

1. Clone the repository
2. Configure environment variables or application properties
3. Ensure the database is running
4. Run Flyway migrations
5. Start the application

### Example

```bash
mvn spring-boot:run
