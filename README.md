# Gifu Core Service

## About The Project

This is a Gifu Core Service

### Built With

This section should list any major frameworks/libraries used to bootstrap your project. Leave any add-ons/plugins for the acknowledgements section. Here are a few examples.

* [Spring Boot](https://spring.io/projects/spring-boot)


<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

This is an example of how to list things you need to use the software and how to install them.
* [JDK 17](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/)

### Installation

Below is an example of how you can instruct your audience on installing and setting up your app. 

1. Install maven dependencies 
   ```shell
   mvn clean install
   ```

2. Run app
   ```shell
   java -jar ./target/*.jar
   ```

<!-- Tips -->
## Create JWT Key
1. ```shell openssl genrsa -out private.pem 2048 ```
2. ```shell openssl rsa -in private.pem -pubout -outform PEM -out public_key.pem ```
3. ```shell openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_key.pem -nocrypt ```

