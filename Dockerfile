# ============================================
# Dockerfile multi-stage para clinica-backend
# ============================================

# ---- Etapa 1: Build ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Cache de dependencias
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copia del código y build
COPY src src
RUN ./mvnw clean package -DskipTests

# ---- Etapa 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Healthcheck contra el endpoint de Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
