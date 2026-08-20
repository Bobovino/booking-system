# Booking System

Spring Boot REST API for hotel rooms and bookings. 
Mostly built this to learn unit, slice and integration testing
plus CI pipelines on top of a basic controller -> service -> repository setup with DTOs and proper error handling.

## Stack

- Java 21, Spring Boot 4
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL
- JUnit 5, Mockito, AssertJ, Testcontainers
- Docker / docker-compose

## Structure

```
controller/   REST endpoints, request/response mapping
service/      business rules (no overlapping bookings for a room)
repository/   Spring Data JPA repos
domain/       JPA entities
dto/          request/response records
exception/    domain exceptions + @RestControllerAdvice for consistent error JSON
```

Controllers never return entities directly, always DTOs. 
This way, we don't expose our database model to the public and we send exactly the info we intend to send to the frontend

## Running it

Copy `.env.example` to `.env` and fill in real values (`.env` is gitignored):

```bash
cp .env.example .env
docker compose up -d db
export $(grep -v '^#' .env | xargs)   # bootRun doesn't load .env on its own
./gradlew bootRun
```

Or just run everything in containers:

```bash
docker compose up --build
```

App runs on `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui.html` · OpenAPI JSON: `/v3/api-docs`
Added Swagger because it was just a library install and the project is better documented this way.

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

- `service/` unit tests with Mockito, mostly around the booking-conflict logic
- `controller/` `@WebMvcTest` slice tests, validation + error mapping
- `integration/` full flow against a real Postgres via Testcontainers

## CI

`.github/workflows/ci.yml` runs `./gradlew build` on every push and PR.
This way we make sure that on every change and commit we didn't break things that we made sure to work before.
Didn't think such a basic project needs a CD pipeline.
