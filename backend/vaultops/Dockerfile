FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY backend/vaultops/pom.xml backend/vaultops/
RUN cd backend/vaultops && mvn dependency:go-offline

COPY backend/vaultops/src backend/vaultops/src
RUN cd backend/vaultops && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/backend/vaultops/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]