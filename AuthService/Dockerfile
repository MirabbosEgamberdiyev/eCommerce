FROM openjdk:21-jdk-slim
COPY target/auth-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]