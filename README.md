# Kafka Spring Boot Demo

This project demonstrates how to integrate **Apache Kafka** with a **Spring Boot** application. The application will produce and consume messages to/from Kafka using the Spring Kafka library.

## Table of Contents
1. [Technologies Used](#technologies-used)
2. [Project Structure](#project-structure)
3. [Running the Application](#running-the-application)
    - [Locally](#running-locally)
    - [Using Docker](#running-using-docker)
4. [Docker Setup](#docker-setup)
5. [Kafka Setup](#kafka-setup)
6. [Testing the Application](#testing-the-application)

## Technologies Used
- **Spring Boot**: For the backend service.
- **Kafka**: Messaging system for producing and consuming messages.
- **Docker**: For containerizing the application.
- **Maven**: Build tool for managing dependencies.

## Project Structure
```
.
├── config
│   └── KafkaConfig.java           # Kafka configuration
├── controller
│   └── LogController.java         # Controller for handling requests
├── model
│   └── Log.java                   # Log model
├── service
│   └── KafkaService.java          # Service to interact with Kafka
├── src
├── target
│   └── ...                        # Build output
├── Dockerfile                     # Docker configuration
├── docker-compose.yml             # Docker Compose configuration
├── pom.xml                        # Maven build file
└── README.md                      # Project README
```

## Running the Application

### Running Locally

1. **Start Kafka**: If you have Kafka installed locally, you can run it on `localhost:9092`. Otherwise, use Docker as shown below.
   
   **Start Zookeeper** (required for Kafka):
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

   **Start Kafka**:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```

2. **Clone the repository** and navigate into the project directory:
   ```bash
   git clone https://github.com/YourUsername/Kafka-SpringBoot-CI-CD-Auth-Demo.git
   cd Kafka-SpringBoot-CI-CD-Auth-Demo
   ```

3. **Build the project** using Maven:
   ```bash
   ./mvnw clean install
   ```

4. **Run the Spring Boot application**:
   ```bash
   ./mvnw spring-boot:run
   ```

   The application will start consuming messages from Kafka. You can send messages to the `logs-topic` to see the application in action.

### Running Using Docker

To run the application and Kafka in Docker, we have provided a `docker-compose.yml` file.

1. **Ensure Docker is installed** on your machine. If not, you can follow the [Docker installation guide](https://docs.docker.com/get-docker/).

2. **Build and run the application with Docker Compose**:
   ```bash
   docker-compose up --build
   ```

   This will build the Spring Boot application and set up a Kafka container using Docker. It will run both Kafka and Zookeeper as well.

3. **Accessing the Application**:
   - The Spring Boot application will be available at `http://localhost:8080` (or the port defined in your `application.properties`).

   - The Kafka broker will be available at `localhost:9092`.

4. **To stop the containers**:
   ```bash
   docker-compose down
   ```

### Docker Setup

Ensure you have the following files in the root of your project:

#### `Dockerfile`
```dockerfile
# Use OpenJDK as base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/Kafka-SpringBoot-CI-CD-Auth-Demo.jar app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### `docker-compose.yml`
```yaml
version: '3.8'

services:
  kafka:
    image: wurstmeister/kafka:latest
    environment:
      KAFKA_ADVERTISED_LISTENER: INSIDE://kafka:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL: PLAINTEXT
      KAFKA_LISTENER_NAME_INSIDE: INSIDE
      KAFKA_LISTENER_PORT: 9092
      KAFKA_LISTENER_INTERNAL: INSIDE
      KAFKA_LISTENER_EXTERNAL: INSIDE
      KAFKA_LISTENER_NAME_EXTERNAL: INSIDE
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper

  zookeeper:
    image: wurstmeister/zookeeper:latest
    ports:
      - "2181:2181"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - kafka
```

This `docker-compose.yml` file sets up the following:
- **Kafka**: Uses the `wurstmeister/kafka` image to run Kafka.
- **Zookeeper**: Uses the `wurstmeister/zookeeper` image to run Zookeeper (Kafka requires Zookeeper).
- **Spring Boot Application**: Builds the Spring Boot application from the `Dockerfile` and links it to the Kafka container.

## Kafka Setup

1. Kafka will run on port `9092` and can be accessed by the Spring Boot application or any Kafka client on `localhost:9092`.
2. The Spring Boot application will use Kafka to send and receive messages from the `logs-topic`.

## Testing the Application

To test the application, you can use Kafka's **console producer** and **consumer**.

### Send Messages to Kafka (Producer)

Run the following command to send messages to the Kafka topic (`logs-topic`):

```bash
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic logs-topic
```

Type your messages and hit Enter to send them to Kafka.

### Consume Messages from Kafka (Consumer)

To consume messages from the Kafka topic, run:

```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic logs-topic --from-beginning
```

Your Spring Boot application will consume messages from `logs-topic` and process them.
