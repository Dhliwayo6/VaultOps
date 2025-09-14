# Inventory Management Tool

A simple inventory management system designed for small to medium-sized businesses in South Africa. Track your assets, monitor their condition, and manage your inventory efficiently.

## 📋 Features

- Track assets in storage and in use
- Monitor asset condition and maintenance
- Record asset serial numbers and types
- Basic CRUD operations for asset management

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.x
- **Database:** MySQL 8.x
- **Build Tool:** Maven
- **Java Version:** 17+

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- MySQL 8.x
- Maven 3.6+

### Setup

1. Clone the repository
```bash
git clone git@github.com:Dhliwayo6/VaultOps.git
cd VaultOps
```

### Configure MYSQL database
mysql -u root -p
CREATE DATABASE vaultops;

### Update application.properties file with database credentials
spring.datasource.url=jdbc:mysql://localhost:3306/vaultops
spring.datasource.username=your_username
spring.datasource.password=your_password

#### Run the application
```bash
mvn spring-boot:run
```