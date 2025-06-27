# 🚀SpringFinalCase

# 🛠Technologies
- Java 21 & Spring Boot 3 (Backend Development)
- Spring Security & JWT (Authentication & Authorization)
- PostgreSQL (Database Management)
- JPA Repository (Database Interaction)
- JUnit & Mockito (Unit Testing)
- Maven (Dependency Management & Build Tool)

# 🔐Security
- The application uses Spring Security with JWT tokens.
- Users are assigned roles with different permission levels.
- Authentication is required for accessing protected endpoints.

# Instructions

## 📥Clone the Repository
```
git clone https://github.com/merveartut/SpringFinalCase.git
cd SpringFinalCase
```
## ⚙️Database Configuration
```
spring.application.name=task-manager
spring.datasource.url =jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```
## 🏗 Run the Application
```
mvn clean install
mvn spring-boot:run
```

## 🧪Run Tests
```
mvn test
```
