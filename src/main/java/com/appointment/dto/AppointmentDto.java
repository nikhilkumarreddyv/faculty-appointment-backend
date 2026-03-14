package com.appointment.dto;

import com.appointment.entity.Appointment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTOs for FacultySlot and Appointment operations.
 */
public class AppointmentDto {

    // ---- Slot Creation Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotRequest {
        @NotNull(message = "Date is required")
        private LocalDate slotDate;

        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        private LocalTime endTime;

        private String notes;
        private Integer maxStudents = 1;
    }

    // ---- Slot Response ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotResponse {
        private Long id;
        private Long facultyId;
        private String facultyName;
        private String facultyDepartment;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean isAvailable;
        private String notes;
    }

    // ---- Book Appointment Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingRequest {
        @NotNull(message = "Slot ID is required")
        private Long slotId;

        @NotNull(message = "Faculty ID is required")
        private Long facultyId;

        private String purpose;
        private String studentNotes;
    }

    // ---- Update Appointment Status ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        @NotNull(message = "Status is required")
        private Appointment.AppointmentStatus status;
        private String facultyNotes;
    }

    // ---- Appointment Response ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentResponse {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private Long facultyId;
        private String facultyName;
        private String facultyDepartment;
        private Long slotId;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String status;
        private String purpose;
        private String studentNotes;
        private String facultyNotes;
        private String createdAt;
    }

    // ---- Faculty Summary DTO ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacultyResponse {
        private Long id;
        private String name;
        private String email;
        private String department;
        private String phone;
        private long availableSlots;
    }
}
