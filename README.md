# cdc-audit-demo

docker-compose down

docker volume rm $(docker volume ls -q)

docker-compose up

curl -X POST -H "Content-Type: application/json" --data @debezium-mysql-connector.json http://localhost:8083/connectors