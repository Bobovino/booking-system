# Booking System

A layered Spring Boot REST API for managing hotel rooms and bookings. Built to demonstrate clean
layering (controller → service → repository), DTO-based API contracts, centralized error handling,
and integration testing with Testcontainers.

## Stack

- Java 21, Spring Boot 4
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL
- JUnit 5, Mockito, AssertJ, Testcontainers
- Docker / docker-compose

## Architecture

```
controller/   REST endpoints, request/response mapping
service/      business rules (e.g. no overlapping bookings for a room)
repository/   Spring Data JPA repositories
domain/       JPA entities
dto/          request/response records, decoupled from entities
exception/    domain exceptions + @RestControllerAdvice → consistent JSON error body
```

Entities are never returned directly from controllers — DTOs keep the API contract independent
from the persistence model.

## Running locally

Copy `.env.example` to `.env` and fill in real values (`.env` is gitignored):

```bash
cp .env.example .env
docker compose up -d db
export $(grep -v '^#' .env | xargs)   # bootRun doesn't auto-load .env like docker compose does
./gradlew bootRun
```

Or run everything (app + database) in containers:

```bash
docker compose up --build
```

The API listens on `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui.html` · OpenAPI JSON: `/v3/api-docs`

## API

| Method | Path                     | Description                     |
|--------|--------------------------|----------------------------------|
| POST   | `/api/rooms`             | Create a room                   |
| GET    | `/api/rooms`             | List rooms                      |
| GET    | `/api/rooms/{id}`        | Get a room                      |
| DELETE | `/api/rooms/{id}`        | Delete a room                   |
| POST   | `/api/bookings`          | Create a booking (409 on overlap) |
| GET    | `/api/bookings`          | List bookings                   |
| GET    | `/api/bookings/{id}`     | Get a booking                   |
| POST   | `/api/bookings/{id}/cancel` | Cancel a booking            |

Example:

```bash
curl -X POST localhost:8080/api/rooms \
  -H 'Content-Type: application/json' \
  -d '{"number":"101","type":"SINGLE","capacity":1,"pricePerNight":49.90}'

curl -X POST localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"roomId":1,"guestName":"Ada Lovelace","guestEmail":"ada@example.com","checkIn":"2030-01-10","checkOut":"2030-01-15"}'
```

## Tests

```bash
./gradlew test
```

- `service/` — unit tests with Mockito for booking-conflict logic
- `controller/` — `@WebMvcTest` slice tests for request validation and error mapping
- `integration/` — full-stack test against a real PostgreSQL container via Testcontainers

## CI

`.github/workflows/ci.yml` runs `./gradlew build` (compile + test) on every push and PR.
