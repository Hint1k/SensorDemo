**Sensor and Measurement Management System** <br>

This is a Java Spring Boot application designed to manage sensors and their measurements. The system provides RESTful APIs for registering sensors, adding measurements, retrieving measurement data, and authenticating users with JWT (JSON Web Tokens). The application is secured using Spring Security, and API documentation is available via Swagger UI.

**Features**<br>
- Sensor Registration: Register new sensors with unique names.
- Measurement Management: Add measurements (temperature and rain status) for registered sensors.
- Data Retrieval: Retrieve all measurements or count the number of rainy days.
- Authentication: Secure access to the API using JWT-based authentication.
- Swagger Documentation: Interactive API documentation available at /swagger-ui.html.

**Technologies Used**<br>
- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT (JSON Web Tokens)
- Docker
- PostgreSQL
- Swagger

**Running the Project with Docker-Compose**<br>
To run the project using Docker-Compose, follow these steps:
- Clone the Repository:<br>
git clone https://github.com/hint1k/SensorDemo.git <br>
cd sensor-measurement-system

- Build the Docker Image: (optional step) <br>
docker-compose build

- Run the Application:<br>
docker-compose up

- Access the Application UI: <br>
Swagger UI can be accessed at http://localhost:8080 as via redirect.

**API Examples**<br>
- Register a Sensor <br>
Endpoint: POST /sensors/registration

Request Body:<br>
{ "name": "Sensor1" }

Response:<br>
{ "status": "success", "message": "Sensor registered successfully" }

- Add a Measurement <br>
Endpoint: POST /measurements/add

Request Body:<br>
{ "temperature": 25.50, "rain": true, "sensor": { "name": "Sensor1" } }

Response:<br>
{ "status": "success", "message": "Measurement added successfully" }

- Get All Measurements <br>
Endpoint: GET /measurements
  
Response:<br>
[ { "id": 1, "temperature": 25.50, "rain": true, "sensor": { "id": 1, "name": "Sensor1" } }, 
{ "id": 2, "temperature": 22.00, "rain": false, "sensor": { "id": 1, "name": "Sensor1" } } ]

- Get Rainy Days Count <br>
Endpoint: GET /measurements/rainyDaysCount

Response:<br>
1

- Authenticate and Get JWT Token<br>
Endpoint: POST /auth/login

Request Parameters:<br>
username: admin<br>
password: 123<br>

Response:<br>
{ "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }

- Access Secured Endpoints<br>
To access secured endpoints, include the JWT token in the Authorization header:<br>
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." http://localhost:8080/measurements<br>

- Database Configuration<br>
The application uses PostgreSQL as the database.<br> 
The connection details are configured in the application.properties file. <br>
The SQL script to create tables and fill in manually in the resources/sql-scripts/init.sql file <br> 

- Swagger json schema is in the swagger.json file<br>
 
**Automatic application test:**
- The class SensorClient is located in /client/src/main/java/com/test/sensor/ <br>
- To test the application automatically: run the docker-compose and then run this class. It will add 1000 measurements to the database and read them from the database. 