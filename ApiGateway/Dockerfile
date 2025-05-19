FROM openjdk:21-jdk-slim
COPY target/gateway.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]