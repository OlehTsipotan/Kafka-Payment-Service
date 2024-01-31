# Payment Service
### A part of [***Spring Kafka Order System***](https://github.com/OlehTsipotan/Spring-Kafka-Order-System)

----
***Work in progress...***

----

It is a service that is responsible for managing (imaginative) payments within the Microservice.
It is responsible for the following tasks:
- *CRUD* operations for Customers and their balance.
- Consuming, processing and producing orders and applying changes to the Customers records.

## Table of Contents
- [Project Description](#project-description)
- [Technology Stack](#technology-stack)
- [How to Install and Run the Project](#how-to-install-and-run-the-project)
- [Credits](#credits)

## Project Description
The Payment Service is designed to provide a flexible and robust API for managing the customers records as a part of the Microservice.
There is REST API for managing product (CRUD operations). It is communicating with [***Order Service***](https://github.com/OlehTsipotan/Kafka-Order-Service/) using Apache Kafka.
### How it works
Order communication schema:
1. Consuming order from the topic `orders`. (Produced by [***Order Service***](https://github.com/OlehTsipotan/Kafka-Order-Service/))
2. Processing order
3. *OPTIONAL* - Producing updated order to the topic `payment-orders`. (Produced by [***Order Service***](https://github.com/OlehTsipotan/Kafka-Order-Service/))

## Technology Stack

- **Backend**:
    - [Java](https://www.java.com/) - A general-purpose programming language that is class-based, object-oriented, and designed to have as few implementation dependencies as possible.
    - [Spring Framework](https://spring.io/) - An application framework and inversion of control container for the Java platform.
        - [Spring Boot](https://spring.io/projects/spring-boot) - An extension of the Spring framework that simplifies the process of building production-ready applications.
        - [Spring Web](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html) - Provides key web-related features, including multipart file upload functionality and initialization of the IoC container.
        - [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Provides a simple and consistent programming model for data access.
        - [Spring MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html) - The web module of the Spring framework that simplifies the work needed to develop web applications.
        - [Spring Kafka](https://spring.io/projects/spring-kafka) - Provides a KafkaTemplate to send messages to Kafka topics, a listener container for asynchronous consumption of messages, and a listener container for synchronous consumption of messages.
    - [PostgreSQL](https://www.postgresql.org/) - An open-source relational database system.
    - [SpringDoc](https://springdoc.org/) - Library helps to automate the generation of API documentation using spring boot projects
    - [Spring Kafka](https://spring.io/projects/spring-kafka) - Provides a KafkaTemplate to send messages to Kafka topics, a listener container for asynchronous consumption of messages, and a listener container for synchronous consumption of messages.
    - [Apache Avro](https://avro.apache.org/) - A data serialization system.

- **Testing**:
    - [Mockito](https://site.mockito.org/) - Mockito is a mocking framework that tastes really good.
    - [JUnit 5](https://junit.org/junit5/) - A programming and testing model for Java applications.
    - [Testcontainers](https://www.testcontainers.org/) - Provides throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.
    - [Flyway](https://flywaydb.org/) - Database migration tool.

- **Development Tools**:
    - [Postman](https://www.postman.com/) - Postman is an API platform for building and using APIs.
    - [Lombok](https://projectlombok.org/) - A Java library that helps to reduce boilerplate code.

- **Containerization and Deployment**:
    - [Docker](https://www.docker.com/) - A platform for developing, shipping, and running applications.

## How to Install and Run the Project
### Go back to the [***Spring Kafka Order System***](https://github.com/OlehTsipotan/Spring-Kafka-Order-System)

> ### Documentation
> - [Swagger UI](http://localhost:8080/swagger-ui.html) -> `http://localhost:8080/swagger-ui.html`

## Credits
Oleh Tsipotan - developer (https://www.linkedin.com/in/oleh-tsipotan/)
