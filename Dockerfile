FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

ARG SPRING_PROFILES_ACTIVE

COPY pom.xml .
COPY src/ /app/src/
RUN mvn dependency:go-offline

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
