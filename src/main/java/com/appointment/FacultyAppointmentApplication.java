package com.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Faculty Appointment Booking System backend.
 */
@SpringBootApplication
public class FacultyAppointmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacultyAppointmentApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Faculty Appointment System - STARTED");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("==============================================");
    }
}
