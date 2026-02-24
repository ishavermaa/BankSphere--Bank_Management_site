# BankSphere--Bank_Management_site
# Bank Management System

A comprehensive bank management system built with Java, Spring Boot, MySQL, and Thymeleaf.

## Features

- User management with roles (Customer, Employee, Admin)
- Account management
- Transaction processing (Deposit, Withdraw, Transfer)
- Role-based dashboards
- Secure authentication

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bank-management
   ```

2. **Create MySQL Database**
   ```sql
   CREATE DATABASE bank_management;
   ```

3. **Update Database Configuration**
   Edit `src/main/resources/application.properties` and update the database credentials:
   ```properties
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the Application**
   Open your browser and go to `http://localhost:8080`

## Default Credentials

- Admin: admin@bank.com / admin123
- Employee: employee@bank.com / emp123
- Customer: customer@bank.com / cust123

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL
- Thymeleaf
- Bootstrap 5

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/bankmanagement/
|   |       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── static/
│       └── templates/
└── test/
```
