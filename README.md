# Kafka Spring Boot CI/CD Demo

This project demonstrates how to integrate **Kafka** with a **Spring Boot** application, leveraging Docker for containerization, and CI/CD for automation using GitHub Actions. The setup is simple and designed for an intermediate level of experience with Kafka and Docker.

## Table of Contents
- [Project Setup](#project-setup)
- [Running Locally](#running-locally)
- [Running with Docker](#running-with-docker)
- [CI/CD Pipeline](#cicd-pipeline)
  - [CI/CD GitHub Actions](#cicd-github-actions)
  - [Required Secrets](#required-secrets)

## Project Setup

### Prerequisites
Before getting started, make sure you have the following tools installed:

- **Docker**: [Install Docker](https://www.docker.com/get-started)
- **Kafka**: [Install Kafka](https://kafka.apache.org/quickstart) or use Docker to run Kafka (recommended)
- **Java JDK 17**: [Download JDK 17](https://adoptopenjdk.net/)
- **Maven**: [Download Maven](https://maven.apache.org/download.cgi)

### Cloning the Repository

Clone the repository to your local machine:

```bash
git clone https://github.com/Nuraj250/Kafka-SpringBoot-CI-CD-Auth-Demo.git
cd Kafka-SpringBoot-CI-CD-Auth-Demo
```

## Running Locally

### 1. Running Kafka Locally

Kafka requires a running instance of ZooKeeper and Kafka itself. You can run Kafka locally using Docker, or you can follow the manual installation steps if preferred. Here's how to do it with Docker:

#### Run Kafka using Docker Compose

1. Make sure you have `docker-compose.yml` file in your project.
2. Run the following command to start Kafka and ZooKeeper:

```bash
docker-compose up -d
```

This will spin up Kafka on `localhost:9092` and ZooKeeper on `localhost:2181`.

### 2. Running the Spring Boot Application

Once Kafka is running, you can start the Spring Boot application by running:

```bash
mvn clean install
mvn spring-boot:run
```

The application should be available at `http://localhost:8080`.

## Running with Docker

To run the Kafka Spring Boot application using Docker:

1. **Build the Docker image**:

   Make sure Docker is running and execute the following command in the project root directory:

   ```bash
   docker build -t kafka-springboot-app .
   ```

2. **Run the Docker container**:

   After the image is built, you can run it using:

   ```bash
   docker run -d -p 8080:8080 kafka-springboot-app
   ```

   This will start the application inside a container, and you can access it at `http://localhost:8080`.

## CI/CD Pipeline

### CI/CD GitHub Actions

This project includes a **CI/CD pipeline** defined using **GitHub Actions**. The pipeline is designed to automatically build, test, and deploy the application on every push to the `main` branch.

The CI/CD pipeline is defined in `.github/workflows/ci-cd.yml`.

#### Key Steps:
1. **Build the Application**: The pipeline compiles the Spring Boot application and runs tests.
2. **Build Docker Image**: The pipeline builds a Docker image for the application and pushes it to Docker Hub.
3. **Deploy to Server**: The pipeline deploys the latest Docker image to a remote server (optional).

### Required Secrets

For the CI/CD pipeline to work, you'll need to configure GitHub secrets for Docker Hub login and SSH access.

1. **DOCKER_USERNAME**: Your Docker Hub username.
2. **DOCKER_PASSWORD**: Your Docker Hub password (or Docker Hub access token).
3. **SSH_PRIVATE_KEY**: Your private SSH key for accessing the server where you want to deploy the Docker container.

To configure these secrets:
1. Go to your GitHub repository.
2. Navigate to **Settings** → **Secrets and variables** → **Actions**.
3. Add the secrets mentioned above.

### Example GitHub Actions Workflow (`ci-cd.yml`)

The CI/CD process is as follows:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          distribution: 'adoptopenjdk'

      - name: Cache Maven dependencies
        uses: actions/cache@v2
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build with Maven
        run: mvn clean install -DskipTests

      - name: Run tests
        run: mvn test

  docker:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2

      - name: Log in to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Build Docker image
        run: |
          docker build -t your-dockerhub-username/kafka-springboot-app .
          docker tag your-dockerhub-username/kafka-springboot-app:latest your-dockerhub-username/kafka-springboot-app:${{ github.sha }}

      - name: Push Docker image
        run: |
          docker push your-dockerhub-username/kafka-springboot-app:latest
          docker push your-dockerhub-username/kafka-springboot-app:${{ github.sha }}

  deploy:
    runs-on: ubuntu-latest
    needs: docker
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Deploy to server
        run: |
          echo "Deploying Docker container..."
          ssh -o StrictHostKeyChecking=no user@your-server-ip "docker pull your-dockerhub-username/kafka-springboot-app:latest && docker run -d -p 8080:8080 your-dockerhub-username/kafka-springboot-app:latest"
```

### How to Trigger the Pipeline

- The pipeline will run automatically whenever there is a push or pull request made to the `main` branch.
- It will:
   - Build the app,
   - Run tests,
   - Build and push the Docker image,
   - Optionally deploy to a remote server.

---

## Conclusion

This project provides a simple Kafka-based Spring Boot application with a CI/CD pipeline using GitHub Actions. It automates the entire process, from building the application to deploying it using Docker.

Let me know if you need any additional changes or explanations on specific parts!
