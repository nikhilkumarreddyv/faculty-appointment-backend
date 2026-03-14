# Faculty Appointment Booking System - Backend

This project is the backend service for the Faculty Appointment Booking System, built with Spring Boot.

## Technology Stack

- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Security:** Spring Security & JWT (JSON Web Token)
- **Database:** MySQL
- **ORM:** Spring Data JPA
- **Build Tool:** Maven
- **Others:** Lombok, DevTools

## Prerequisites

- JDK 17 or higher
- MySQL Server
- Maven (or use the provided `mvnw` wrapper)

## Setup and Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/nikhilkumarreddyv/faculty-appointment-backend.git
   ```

2. **Database Configuration:**
   Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/faculty_appointment_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run the application:**
   Using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or if Maven is installed:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

The backend provides endpoints for:
- User Authentication (Login/Signup)
- Faculty Dashboard
- Student Booking
- Appointment Management

## License

This project is licensed under the MIT License.
