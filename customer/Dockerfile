# Use an official OpenJDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/customer-service-0.0.1-SNAPSHOT.jar /app/customer.jar
ENTRYPOINT ["java", "-jar", "/app/customer.jar"]