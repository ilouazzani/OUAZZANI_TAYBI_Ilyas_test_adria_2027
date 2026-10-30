# Multi-stage build for ai-agent-service
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml ../pom.xml
COPY ai-agent-service/pom.xml ./pom.xml
COPY ai-agent-service/src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
