# be-springboot-auth-service

**Backend Service for Authentication Using Spring Boot**

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [CI/CD](#cicd)
- [Code Quality](#code-quality)
- [Contributing](#contributing)
- [License](#license)

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
- Role-based access control (RBAC)
- Integration-ready for front-end and microservices
- Full unit and integration tests

---

## Architecture

- **Spring Boot 3.2+** with Java 21
- **Maven** for dependency management
- **PostgreSQL** as the primary database
- **Flyway** for database migrations (or Liquibase, choose one)
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
cd be-springboot-auth-service
