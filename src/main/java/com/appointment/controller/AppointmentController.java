package com.appointment.controller;

import com.appointment.dto.AppointmentDto;
import com.appointment.entity.User;
import com.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for appointment booking, viewing, and management.
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * POST /api/appointments/book
     * Book an appointment (student only).
     */
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody AppointmentDto.BookingRequest request,
            @AuthenticationPrincipal User currentUser) {
        try {
            AppointmentDto.AppointmentResponse response =
                    appointmentService.bookAppointment(currentUser.getId(), request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/appointments/student/{studentId}
     * Get all appointments for a student.
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getStudentAppointments(
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(appointmentService.getStudentAppointments(studentId));
    }

    /**
     * GET /api/appointments/faculty/{facultyId}
     * Get all appointments for a faculty member.
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getFacultyAppointments(
            @PathVariable("facultyId") Long facultyId) {
        return ResponseEntity.ok(appointmentService.getFacultyAppointments(facultyId));
    }

    /**
     * PUT /api/appointments/{id}/status
     * Faculty accepts or rejects an appointment request.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppointmentDto.StatusUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        try {
            AppointmentDto.AppointmentResponse response =
                    appointmentService.updateAppointmentStatus(id, currentUser.getId(), request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/appointments/{id}
     * Cancel an appointment (student or faculty).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User currentUser) {
        try {
            AppointmentDto.AppointmentResponse response =
                    appointmentService.cancelAppointment(id, currentUser.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment cancelled successfully",
                    "appointment", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/appointments/all
     * Get all appointments (admin only).
     */
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
}
