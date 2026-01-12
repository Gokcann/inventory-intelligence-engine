# Inventory Intelligence Engine

Event-Driven Modular Monolith built with Java 21 (Virtual Threads), Spring Boot 3.4+, and Spring Modulith.

## Tech Stack
- **Java 21** with Virtual Threads (Project Loom)
- **Spring Boot 3.4+** with Spring Modulith
- **PostgreSQL 17** with native partitioning
- **Redis 7** with Lua scripting for atomic operations
- **Kafka** (KRaft mode) for event streaming

## Quick Start

```bash
# Start the application (Docker Compose will auto-start)
./mvnw spring-boot:run
```

## Architecture

This project follows the **Islands Architecture** pattern:
- Strict module isolation (no cross-module internal access)
- Event-driven communication between modules
- API-First development with OpenAPI contracts

## Modules
- `inventory` - Stock management, reservations
- `order` - Order processing, lifecycle
- `shared` - Utilities only (NO business logic)
