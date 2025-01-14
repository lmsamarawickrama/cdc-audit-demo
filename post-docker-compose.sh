#!/bin/bash
echo "Opening URLs in the default web browser..."
xdg-open http://localhost:8091 &  # Adminer
xdg-open http://localhost:9001 &  # Kafdrop
xdg-open http://localhost:8084/subjects &  # Schema Registry
xdg-open http://localhost:8083/connectors &  # Kafka Connect
echo "Post Docker Compose actions completed."
