FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY . .
RUN cd backend/vaultops && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /build/backend/vaultops/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "vaultops-0.0.1-SNAPSHOT.jar"]