FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/product-service.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app.jar"]