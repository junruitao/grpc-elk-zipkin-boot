# Currency Conversion System

This system demostrates following cloud native usage:
* Logging and tracing using the ELK stack
* Implementing logging and tracing in the gRPC code
* Distributed tracing with Zipkin and Micrometer
* Managing containers with `Docker Compose`


## Conversion Service
http://localhost:8081/convert/USD/JPY/120

## Kibana dashboard
http://localhost:5600

## Zipkin Service
http://localhost:9411/zipkin/

## docker compose
```
    docker compose up
```

## Useful Commands

| Gradle Command	         | Description                                   |
|:---------------------------|:----------------------------------------------|
| `./gradlew clean`        | Clean  application.                          |:----------------------------------------------|
| `./gradlew bootRun`        | Run the application.                          |
| `./gradlew build`          | Build the application.                        |
| `./gradlew test`           | Run tests.                                    |
| `./gradlew bootJar`        | Package the application as a JAR.             |
| `./gradlew bootBuildImage` | Package the application as a container image. |
