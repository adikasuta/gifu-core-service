FROM maven:3.6-jdk-11 as maven
COPY pom.xml /
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package -DskipTests  && cp target/*.jar cybersource-middleware.jar

FROM openjdk:11.0.7-jre-slim
WORKDIR /application
COPY --from=maven /cybersource-middleware.jar ./cybersource-middleware.jar
CMD echo "The application is starting..." && \
 java \
 -jar cybersource-middleware.jar ${APP_CONFIG_ARGS}
