# ---------- STAGE 1: Build the application ----------
# Use the official Maven image with JDK 21 (Eclipse Temurin)
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Optional build argument for Spring profile (e.g., dev, prod)
ARG SPRING_PROFILES_ACTIVE

# Copy only the Maven configuration file first
# This allows Docker to cache dependencies if pom.xml hasn’t changed
COPY pom.xml .

# Pre-download all dependencies to speed up subsequent builds
# (cached layer – only re-runs if pom.xml changes)
RUN mvn dependency:go-offline

# Now copy the actual source code into the container
COPY src/ /app/src/

# Build the Spring Boot JAR package (skip tests to save time)
RUN mvn clean package -DskipTests


# ---------- STAGE 2: Create the runtime image ----------
# Use a lightweight JRE base image for running the application
FROM eclipse-temurin:21-jre

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built JAR from the builder stage into this runtime image
COPY --from=builder /app/target/*.jar app.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Define the startup command to run the application
CMD ["java", "-jar", "app.jar"]