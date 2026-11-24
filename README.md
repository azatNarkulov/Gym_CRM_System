# Gym CRM System

## Stack

| Layer       | Technology                       |
|-------------|----------------------------------|
| Language    | Java 25                          |
| Framework   | Spring Boot 3                    |
| Data Access | Spring Data JPA (Hibernate 6)    |
| DB          | PostgreSQL 16                    |
| Build       | Maven                            |
| Logging     | SLF4J + Logback                  |
| Testing     | JUnit 5, Mockito                 |
| API Docs    | Swagger / OpenAPI 3 (Springdoc)  |
| Monitoring  | Spring Boot Actuator, Prometheus |

## Project structure

- `gym-crm-app/` — Gym CRM Maven application
- `trainer-workload-service/` — Trainer Workload Maven application
- `eureka-server/` — Eureka Discovery Server Maven application

## API documentation

- Gym CRM: `http://localhost:8080/swagger-ui/index.html`
- Trainer Workload: `http://localhost:8081/swagger-ui/index.html`
