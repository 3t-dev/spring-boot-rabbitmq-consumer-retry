# Spring-boot RabbitMQ Consumer Retry

## Introduction

This is example for RabbitMQ consumer retry handling for Spring-boot application.
Following list is including features:

* Full RabbitMQ producer-consumer messaging flow.
* Consumer retry implement using RabbitMQ.
* Dead-Lettering advance feature:
  * Reject & don't re-queue message.
  * Dead-Letter-Exchange to pass dead message.
  * Message time-to-live to handle delayed retry.
  * Parking-lot queue to store exceed retry times messages.

## Get started

### Configuration

RabbitMQ connection information is defined in application.properties file:

```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=demo_username
spring.rabbitmq.password=demo_password
spring.rabbitmq.virtual-host=demo
```

### Run application

```
mvn spring-boot:run
```

## Contact
Email to <trinhthethanh25390@gmail.com>