#!/bin/bash

# Wait for Kafka Connect to be ready
echo "Waiting for Kafka Connect to be ready..."
until $(curl --output /dev/null --silent --head --fail http://localhost:8083/connectors); do
    printf '.'
    sleep 5
done

# Register the Debezium MySQL connector
echo "Registering Debezium MySQL connector..."
curl -X POST -H "Content-Type: application/json" --data @debezium-mysql-connector.json http://localhost:8083/connectors

# Open URLs in the default web browser
echo "Opening URLs in the default web browser..."
xdg-open http://localhost:8091 &  # Adminer
xdg-open http://localhost:8084/subjects &  # Schema Registry
xdg-open http://localhost:8083/connectors/mysql-connector &  # Kafka Connect
xdg-open http://localhost:9001 &  # Kafdrop

echo "Post Docker Compose actions completed."
