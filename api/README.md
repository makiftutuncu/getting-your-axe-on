# Battleships API

## Table of Contents

1. [Introduction](#introduction)
2. [Configuration](#configuration)
3. [Local Development](#local-development)
4. [API](#api-docs)

## Introduction

Battleships API is a game server for the Battleships game.

It uses

* Maven for builds
* Spring Boot WebFlux 3 for web backend
* Kotlin 2 for programming language
* Axon Framework for event sourcing
* PostgreSQL for database
* Spring Data JPA for database access
* JUnit 5 for testing

## Configuration

Configuration can be modified via [application.yml](src/main/resources/application.yml) or following environment variables. The default values allow you to run Battleships API out-of-the-box.

| Variable Name | Data Type | Description                          | Required                               |
|---------------|-----------|--------------------------------------|----------------------------------------|
| DB_HOST       | String    | Host address of application database | No, defaults to `localhost`            |
| DB_PORT       | Integer   | Port of application database         | No, defaults to `5432`                |
| DB_NAME       | String    | Name of application database         | No, defaults to `battleships-db`       |
| DB_USER       | String    | User of application database         | No, defaults to `battleships-user`     |
| DB_PASS       | String    | Password of application database     | No, defaults to `battleships-password` |
| DB_PARAMS     | String    | Additional parameters for database   | No, defaults to empty string           |

## Local Development

Java 21+ is required. You can use Maven tasks like `clean`, `compile`, `install` and `test` for local development. To run the application:

```shell
mvn spring-boot:run
```

If you don't have Maven installed, you can replace `mvn` commands with `./mvnw` to use Maven wrapper.

## API Docs

Battleships API provides OpenAPI documentation and a Swagger UI to browse them. After running the application, you may go to [/swagger-ui.html](http://localhost:8080/swagger-ui.html) to launch Swagger UI.
