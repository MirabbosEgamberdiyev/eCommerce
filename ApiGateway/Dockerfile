FROM eclipse-temurin:21-jdk-alpine
LABEL authors="mirabbos.egamberdiye"

WORKDIR /app

COPY target/gateway.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
