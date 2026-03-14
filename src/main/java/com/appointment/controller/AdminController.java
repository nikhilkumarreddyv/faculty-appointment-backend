package com.appointment.controller;

import com.appointment.dto.AppointmentDto;
import com.appointment.entity.User;
import com.appointment.repository.UserRepository;
import com.appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin operations - manage users and view all appointments.
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentService appointmentService;

    /**
     * GET /api/admin/students
     * Get all students.
     */
    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getAllStudents() {
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : students) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("department", u.getDepartment() != null ? u.getDepartment() : "");
            map.put("phone", u.getPhone() != null ? u.getPhone() : "");
            map.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/faculty
     * Get all faculty.
     */
    @GetMapping("/faculty")
    public ResponseEntity<List<Map<String, Object>>> getAllFaculty() {
        List<User> faculty = userRepository.findByRole(User.Role.FACULTY);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : faculty) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("department", u.getDepartment() != null ? u.getDepartment() : "");
            map.put("phone", u.getPhone() != null ? u.getPhone() : "");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/appointments
     * Get all appointments in the system.
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto.AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * DELETE /api/admin/users/{id}
     * Delete a user from the system.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}
