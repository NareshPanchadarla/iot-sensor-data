## Use official Maven image to build the app
#FROM maven:3.9.6-eclipse-temurin-21 AS build
#WORKDIR /app
#
## Copy project files
#COPY . .
#
## Run Maven build (this will generate the JAR)
#RUN mvn clean package -DskipTests
#
## Use an official OpenJDK runtime as a base image
#FROM eclipse-temurin:21-jre-alpine
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy Maven configuration and source files
#COPY pom.xml .
#COPY src ./src
#
## Copy the JAR file from the target directory to the container
#COPY --from=build /app/target/iot-sensor-data-0.0.1-SNAPSHOT.jar /app/iot-sensor-data-0.0.1-SNAPSHOT.jar
#
## Expose port 8080 to communicate with the outside world
#EXPOSE 8080
#
## Run the JAR file when the container starts
#ENTRYPOINT ["java", "-jar", "/app/iot-sensor-data-0.0.1-SNAPSHOT.jar"]
