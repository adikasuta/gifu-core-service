FROM maven:3.8.5-openjdk-17 as maven
COPY pom.xml /
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package -DskipTests  && cp target/*.jar gifu-core-service.jar

FROM openjdk:17
WORKDIR /application
COPY --from=maven /gifu-core-service.jar ./gifu-core-service.jar
CMD echo "The application is starting..." && \
 java \
 -Dspring.profiles.active=staging -jar gifu-core-service.jar
