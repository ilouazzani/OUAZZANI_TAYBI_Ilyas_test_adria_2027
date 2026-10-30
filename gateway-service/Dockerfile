# Multi-stage build for gateway-service
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml ../pom.xml
COPY gateway-service/pom.xml ./pom.xml
COPY gateway-service/src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
