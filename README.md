# Horizon Saga Distributed System

This project implements a production-like microservices architecture using gRPC, PostgreSQL, Docker, and GitHub Actions.

## Components
- `order-orchestrator`: Saga orchestrator service with REST UI API and gRPC orchestration.
- `order-service`: order state storage service.
- `kitchen-service`: kitchen ticket creation and cancellation service.
- `accounting-service`: payment authorization service.
- `order-common`: shared protobuf definitions.
- `ui`: simple browser UI page.

## Running Locally
1. Build the project:
   ```bash
   mvn clean package -DskipTests
   ```
2. Start development containers:
   ```bash
   docker-compose -f docker-compose.dev.yml up --build
   ```
3. Open `ui/index.html` in your browser and use the REST API at `http://localhost:8080`.

## Production Compose
Use `docker-compose.prod.yml` to run the setup with published images:
```bash
docker-compose -f docker-compose.prod.yml up
```

## CI/CD
The GitHub Actions workflow builds the Maven modules, packages artifacts, builds Docker images, and pushes them to Docker Hub using secrets:
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

## Saga Behavior
- Happy path: order is created as `APPROVAL_PENDING`, kitchen ticket created, payment authorized, order approved.
- Compensation path: if payment fails, kitchen ticket is cancelled and order state is updated to `REJECTED`.
