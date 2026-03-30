# Stage 1: Build
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build
COPY . .
RUN ./mvnw install -DskipTests -T4C -Dquarkus.config.locations=server.docker.exemple.properties

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=builder /build/app/target/quarkus-app/ /app/
COPY --from=builder /build/server.docker.properties /app/server.docker.properties
COPY --from=builder /build/jwt /app/jwt

EXPOSE 8080

CMD ["java", "-Dquarkus.config.locations=server.docker.properties", "-jar", "quarkus-run.jar"]