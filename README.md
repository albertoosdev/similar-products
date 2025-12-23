# Similar Products API

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-green?style=for-the-badge&logo=spring)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)
![Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen)

A high-performance, resilient REST API designed to aggregate similar product details. This project implements a **Hexagonal Architecture** to orchestrate data from external services, featuring caching strategies and circuit breakers for fault tolerance.

---

## 📋 Table of Contents

1. [Architecture & Design](#-architecture--design)
2. [Tech Stack](#-tech-stack)
3. [Prerequisites](#-prerequisites)
4. [Getting Started](#-getting-started)
5. [API Reference](#-api-reference)
6. [Performance Testing](#-performance-testing)
7. [Configuration](#-configuration)

---

## 🏗 Architecture & Design

This application follows the **Hexagonal Architecture (Ports and Adapters)** pattern to decouple the core domain logic from external dependencies.

* **Domain Layer:** Contains the business logic (`UseCase`) and models. No frameworks here.
* **Infrastructure Layer:**
    * **Inbound:** REST Controller exposed via OpenAPI contract.
    * **Outbound:** REST Adapter using `RestClient` to consume external APIs.
* **Resilience:** Implemented using **Resilience4j** (Circuit Breaker) to handle downstream failures gracefully.
* **Performance:** Uses **Caffeine Cache** (In-Memory) to reduce latency for recurring requests.

---

## 🛠 Tech Stack

* **Core:** Java 21, Spring Boot 3.4
* **Documentation:** OpenAPI 3.0 (Contract-First), Swagger UI
* **Mapping:** MapStruct
* **Resilience:** Resilience4j (Circuit Breaker)
* **Testing:** JUnit 5, Mockito, WireMock, k6
* **Containerization:** Docker, Docker Compose

---

## 📋 Prerequisites

* **Docker** & **Docker Compose** (Recommended)
* Java 21 & Maven (Only if running locally without Docker)

---

## 🚀 Getting Started

### Option A: Run with Docker (Recommended)

This command builds the application image and starts the full environment (App + Mocks + Monitoring).

```bash
docker-compose up --build -d
```

> **Note:** The application will be available at port **5000**.

| Service | URL | Description |
| :--- | :--- | :--- |
| **Main API** | `http://localhost:5000` | The Similar Products Application |
| **Mock API** | `http://localhost:3001` | Simulates the external product service |
| **Grafana** | `http://localhost:3000` | Visualization for performance tests |

### Option B: Run Locally

If you prefer to run the JAR manually, ensure the Mock API is running on port 3001 or configure the URL in `application.yml`.

```bash
./mvnw clean spring-boot:run
```

## 🔌 API Reference

The API definition is available via Swagger UI.

👉 **Interactive Documentation:** [http://localhost:5000/swagger-ui.html](http://localhost:5000/swagger-ui.html)

### Key Endpoint

`GET` `/product/{productId}/similar`

**Example Request (cURL):**

```bash
curl -v http://localhost:5000/product/1/similar
```

## ⚡ Performance Testing

We provide a specialized **k6** load testing suite to validate resilience and caching.

### 1. Prepare Environment
Ensure your Docker file sharing settings allow mounting the `./shared` folder if you are on Windows/Mac.

Start the infrastructure (excluding the test runner):

```bash
docker-compose up -d simulado influxdb grafana app
```

### 2. Verify Mocks
Ensure the external mock service is responsive:
[http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids)

### 3. Run the Load Test
Execute the test script. This container will attach to the network, run the stress test, and exit.

```bash
docker-compose run --rm k6 run scripts/test.js
```

### 4. Visualize Results
Open Grafana to see real-time metrics (Throughput, Latency, Error Rate).
👉 **Dashboard:** [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test)

## ⚙ Configuration
You can customize the application behavior using environment variables in `docker-compose.yml`:

| Variable | Default | Description |
| :--- | :--- | :--- |
| `SERVER_PORT` | `5000` | Port where the API listens |
| `PRODUCTS_API_URL` | `http://localhost:3001` | URL of the external backend service |
| `LOGGING_LEVEL_APP` | `INFO` | Log level (DEBUG, INFO, ERROR) |