# Use an official OpenJDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/audit-ingestion-0.0.1-SNAPSHOT.jar /app/audit-ingestion.jar
ENTRYPOINT ["java", "-jar", "/app/audit-ingestion.jar"]