FROM openjdk:21-jdk-slim
COPY target/notification-service.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app.jar"]