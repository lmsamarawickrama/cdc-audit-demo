# cdc-audit-demo

## Architecture Overview

The architecture consists of several components, each playing a crucial role in ensuring reliable data capture for auditing. The following diagram provides a high-level overview of the architecture:

![Architecture Diagram](https://via.placeholder.com/800x400?text=Architecture+Diagram)

## Components Description

### Customer Service

- Handles customer-related operations.
- Exposes REST APIs for creating, updating, and retrieving customer data.

### Audit Enrichment Service

- Listens to relevant Kafka topics created for each table (e.g., `app_main.APP_MAIN.customer`, `app_main.APP_MAIN.address`) to store events captured via CDC.
- Enriches and processes data to create uniform audit trail entries.
- Publishes enriched data to a common audit topic (`app_main.APP_MAIN.audit`).

### Audit Ingestion Service

- Listens to the common audit topic (`app_main.APP_MAIN.audit`).
- Persists data to the `app_audit` database.

### Kafka

- Message broker for communication between services.
- Ensures reliable data transfer with order guarantees per partition.

### Kafka Connect

- Connects Kafka with external systems.
- Uses Debezium to capture changes in the `app_main` database and publish them to relevant Kafka topics.

### Schema Registry

- Manages Avro schemas for Kafka messages.
- Ensures compatibility between producers and consumers.

### Adminer

- Web-based database management tool.
- Provides an interface to manage the MySQL database.

### Kafdrop

- Web UI for Kafka.
- Provides insights into Kafka topics, consumers, and messages.

## Data Flow

The following diagram illustrates the flow of data from the customer service to the audit trail database:

![Data Flow Diagram](https://via.placeholder.com/800x400?text=Data+Flow+Diagram)

### Data Flow Steps

1. **Customer Service**
   - A customer creates or updates their information via the customer service API.
   - The customer service writes the changes to the `app_main` MySQL database.

2. **Debezium Connector**
   - Debezium captures changes in the `app_main` database.
   - The changes are published to relevant Kafka topics (e.g., `app_main.APP_MAIN.customer`, `app_main.APP_MAIN.address`).

3. **Kafka**
   - Kafka brokers the messages between services.
   - The messages are stored in Kafka topics with order guarantees per partition.

4. **Audit Enrichment Service**
   - The audit enrichment service consumes messages from the relevant Kafka topics.
   - It enriches and processes the data to create uniform audit trail entries.
   - The enriched data is published to the common audit topic (`app_main.APP_MAIN.audit`).

5. **Audit Ingestion Service**
   - The audit ingestion service consumes messages from the common audit topic.
   - It processes the messages and stores audit logs in the `app_audit` database.

## Handling Duplicates and Correlation

- **Unique Constraint**: The `correlation_id` and `deduplication_id` combination creates a unique constraint in the `app_audit` database to handle duplicates.
- **Exactly Once Semantics**: Kafka Streams handles duplication via exactly-once semantics.
- **Correlation ID**: The `correlation_id` is the same for all updates to any table within the same transaction. It can be used to correlate and aggregate related transactions in the audit logs.

## Order Guarantee with Kafka

Kafka guarantees the order of messages per partition. This ensures that messages are consumed in the same order they were produced, maintaining the integrity of the data flow.

docker-compose down

docker volume rm $(docker volume ls -q)

docker-compose up

curl -X POST -H "Content-Type: application/json" --data @debezium-mysql-connector.json http://localhost:8083/connectors