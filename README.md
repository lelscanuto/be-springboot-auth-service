# be-springboot-auth-service

**Backend Service for Authentication Using Spring Boot**

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)

---

## Overview

This repository contains a **backend authentication service** built with **Spring Boot 3.2+** and **Java 21**, providing
secure login, user management, and session handling.

It is designed for **high maintainability**, **trunk-based development**, and **modern CI/CD workflows**.

---

## Features

- User registration and login
- JWT-based authentication
- Password hashing and validation
- Integration-ready for front-end and microservices
- Full unit and integration tests

---

## Architecture

- **Spring Boot 3.2+** with Java 21
- **Maven** for dependency management
- **PostgreSQL** as the primary database
- **Flyway** for database migrations
- **SonarCloud** for CI/CD code quality and security analysis
- **SonarQube** locally for developer feedback
- Optional **Docker containerization** for local development

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL 15+
- Docker (optional for local dev)

### Clone and Run

```bash
git clone git@github.com:lelscanuto/be-springboot-auth-service.git
```

### Accessing the swagger-ui

```bash
http://localhost:8080/swagger-ui/index.html#/
```