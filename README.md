# be-springboot-auth-service

![Java](https://img.shields.io/badge/Java-21-blue)
![Maven](https://img.shields.io/badge/Maven-3.8+-brightgreen)
![Build](https://github.com/lelscanuto/be-springboot-auth-service/actions/workflows/build.yml/badge.svg)
![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=lelscanuto_be-springboot-auth-service&metric=alert_status)

**Backend Service for Authentication Using Spring Boot**

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Running Locally](#running-locally)
- [Accessing Swagger UI](#accessing-swagger-ui)
- [API Documentation](#api-documentation)

---

## Overview

This repository contains a **backend authentication service** built with **Spring Boot 3.2+** and **Java 21**.  
It provides secure login, user management, and session handling, following **Hexagonal / Clean Architecture** principles
for high maintainability and testability.

It is designed for **trunk-based development**, **modern CI/CD workflows**, and **easy integration** with front-end or
other microservices.

---

## Features

- User registration and login
- JWT-based authentication
- Password hashing and validation
- Integration-ready for front-end and microservices
- Full unit and integration tests

---

## Architecture

- **Hexagonal / Clean Architecture** with clearly separated layers:
    - **Domain** – Core business logic
    - **Application** – Use cases / services
    - **Adapters** – Persistence, web, messaging, external services
- **Spring Boot 3.2+** with Java 21
- **Maven** for dependency management
- **PostgreSQL** as the primary database
- **Flyway** for database migrations
- **SonarCloud** for CI/CD code quality and security analysis
- Optional **Docker containerization** for local development

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL 15+
- Docker (optional for local dev)

### Clone and Install Dependencies

```bash
git clone git@github.com:lelscanuto/be-springboot-auth-service.git
cd be-springboot-auth-service
mvn clean install
```

## Running Locally

```bash
mvn spring-boot:run
```

## Accessing Swagger UI

```bash
http://localhost:8081/swagger-ui/index.html#/
```

## API Documentation

You can view the generated API documentation here:

[📘 View API Docs](docs/api-doc.html)