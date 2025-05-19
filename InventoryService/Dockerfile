FROM openjdk:21-jdk-slim
COPY target/inventory-service.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]