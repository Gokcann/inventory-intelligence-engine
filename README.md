# Inventory Intelligence Engine

[![CI](https://github.com/YOUR_USERNAME/inventory-intelligence-engine/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/inventory-intelligence-engine/actions/workflows/ci.yml)

Event-Driven Modular Monolith built with Java 21 (Virtual Threads), Spring Boot 3.4+, and Spring Modulith.

## Features

- **JWT Authentication** - Secure API with Bearer tokens
- **Prometheus Metrics** - Custom business metrics at `/actuator/prometheus`
- **Swagger UI** - API docs at `/swagger-ui/index.html`
- **Lua Scripting** - Atomic Redis operations for stock reservations
- **Event-Driven** - Domain events with Kafka externalization

## Tech Stack

| Component | Technology |
|-----------|------------|
| Runtime | Java 21 (Virtual Threads) |
| Framework | Spring Boot 3.4+ / Spring Modulith |
| Database | PostgreSQL 17 |
| Cache | Redis 7 (Lua scripts) |
| Events | Kafka (KRaft mode) |

## Quick Start

```bash
# Start infrastructure
docker compose up -d

# Run application
./mvnw spring-boot:run

# Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

# Use API
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/inventory/SKU001
```

## Demo Users

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Get JWT token |
| GET | `/inventory/{sku}` | Get stock level |
| POST | `/inventory/{sku}` | Set stock level |
| POST | `/inventory/{sku}/reserve` | Reserve stock |
| GET | `/actuator/prometheus` | Metrics |
| GET | `/swagger-ui/index.html` | API docs |

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  Spring Boot App                 │
├─────────────┬─────────────┬─────────────────────┤
│  inventory  │    order    │       shared        │
│  module     │   module    │   (events, utils)   │
├─────────────┴─────────────┴─────────────────────┤
│        PostgreSQL  │  Redis  │  Kafka            │
└─────────────────────────────────────────────────┘
```

## Running Tests

```bash
./mvnw test
```

## License

MIT
