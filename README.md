# Sensor Data Management API
A REST API built with Spring Boot, Kafka, MySQL, JWT Authentication, and Avro Schema.

## Environment:
- **Java version**: 21
- **Maven version**: 3.8.x
- **Spring Boot version**: 3.3.9
- **Database**: MySQL (Dockerized)
- **Kafka & Zookeeper**: Dockerized

## Implementation:
- REST API with **Spring Boot** and **Spring Data JPA**
- JWT-based **Spring Security** implementation for authentication
- Integration with **Kafka** for data streaming
- **Avro schema** for data serialization

## How to run the Application
 ### Note: The Spring Boot application is not run using Docker Compose to avoid long download times for all Confluent libraries. Instead, only the required Avro library is added manually via script for efficiency.
 ### Kafka, Zookeeper, and MySQL run in Docker Compose.
### Steps:
1. Ensure **Docker**, **Docker Compose**, and **Docker UI** are installed on your system.
2. Navigate to the project root directory of the Spring Boot sensor project.
3. Run `docker-compose up --build -d` to start the required services (Kafka, Zookeeper, Schema Registry, MySQL).
4. Verify that the following containers are running:
    - Kafka
    - MySQL
    - Schema Registry
    - Zookeeper
5. Register the Avro schema manually by running the following script:
   ```bash
   ./init-schema.sh
   ```
6. If `init-schema.sh` does not execute, make the script executable by running:
   ```bash
   chmod +x init-schema.sh
   ```
   Then run the script again.
7. Finally, run the Spring Boot application locally in your IDE.

Now, you should be able to start the Spring Boot application and connect to the running services.

## REST API Documentation: Open API / Swagger
 - Swagger UI : http://localhost:8080/swagger-ui.html
 - Postman Json collection : http://localhost:8080/v3/api-docs

# We have enabled the JWT authorization, to access endpoints we have to follow the below two steps.
- Step 1: Create User: Send a **POST** request to `http://localhost:8080/api/v1/sensors/register`
- The response code is 201, and the response body is the created User object.
- Example of JSON Request object:
```
{
     "username":  "Test",
     "email": "Test@gmail.com",
     "password": 1234675688 
}
```
- Step 2: Login with Created User: **POST** request to `http://localhost:8080/api/v1/sensors/login`
- It authenticates the user and return the JWT token with response code 200.
- Example of JSON Request object:
```
   {
    "email": "Test@gmail.com",
    "password": 1234675688
    }
```

**Using JWT Token**:  
Now use the JWT token for subsequent requests. In Postman, select **Authorization** tab:
- Choose **Bearer Token**.
- Paste the JWT token from the login response.

You can now access the protected endpoints.
   - Note: Token has an expiration for 1 hour. when you get unauthorized error. please repeat the step 2.

## Pre-existing Data
- **Note**:  
  The application has pre-inserted data via **Liquibase** during the Spring Boot application startup. The following request body can be used to query and verify results from the pre-populated data.

    - **Insert Queries**:  
      To see the insert queries used for data population, check the **Liquibase** changelog file located at:
      ```
      src/main/resources/liquibase/changelog-master.xml
      ```
## End Points Related to ** Sensor Readings **
- Get Sensor Readings **POST** request to http://localhost:8080/api/v1/sensors/readings
  - - Example of JSON Request body:
  ```
  {
      "sensorId" : "THERMO_001" ,
      "sensorType" : "THERMOSTAT",
      "startTime" : "2025-03-10T08:15:00",
      "endTime" :  "2025-03-19T21:15:00"  
  }
  ```
- Get Group Sensor Readings **POST** request to http://localhost:8080/api/v1/sensors/readings/group
  - - Example of JSON Request body:
  ```
  {
          "sensorGroup": [
           {"sensorId": "THERMO_001", "sensorType": "THERMOSTAT"},
           {"sensorId": "HR_001", "sensorType": "HEART_RATE"},
           {"sensorId": "FUEL_001", "sensorType": "CAR_FUEL_LEVEL"}
          ],
          "startTime": "2025-03-10T08:15:00",
          "endTime": "2025-03-10T18:15:00"
  }
  ```
