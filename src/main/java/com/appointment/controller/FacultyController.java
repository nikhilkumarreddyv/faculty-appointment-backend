package com.appointment.controller;

import com.appointment.dto.AppointmentDto;
import com.appointment.entity.User;
import com.appointment.repository.UserRepository;
import com.appointment.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for faculty listing and slot management.
 */
@RestController
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private SlotService slotService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/faculty
     * Get all faculty members with available slot counts.
     */
    @GetMapping
    public ResponseEntity<List<AppointmentDto.FacultyResponse>> getAllFaculty() {
        return ResponseEntity.ok(slotService.getAllFacultyWithSlots());
    }

    /**
     * GET /api/faculty/{id}
     * Get a single faculty member's profile.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFacultyById(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == User.Role.FACULTY)
                .map(u -> ResponseEntity.ok(Map.of(
                        "id", u.getId(),
                        "name", u.getName(),
                        "email", u.getEmail(),
                        "department", u.getDepartment() != null ? u.getDepartment() : "",
                        "phone", u.getPhone() != null ? u.getPhone() : "")))
                .orElse(ResponseEntity.notFound().build());
    }
}
