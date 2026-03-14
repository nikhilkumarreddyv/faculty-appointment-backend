package com.appointment.controller;

import com.appointment.dto.AppointmentDto;
import com.appointment.entity.User;
import com.appointment.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for faculty slot management.
 */
@RestController
@RequestMapping("/slots")
public class SlotController {

    @Autowired
    private SlotService slotService;

    /**
     * GET /api/slots/{facultyId}
     * Get all available slots for a specific faculty member.
     */
    @GetMapping("/{facultyId}")
    public ResponseEntity<List<AppointmentDto.SlotResponse>> getAvailableSlots(
            @PathVariable("facultyId") Long facultyId) {
        return ResponseEntity.ok(slotService.getAvailableSlotsByFaculty(facultyId));
    }

    /**
     * GET /api/slots/{facultyId}/all
     * Get all slots (available + booked) for a faculty member.
     */
    @GetMapping("/{facultyId}/all")
    public ResponseEntity<List<AppointmentDto.SlotResponse>> getAllSlots(
            @PathVariable("facultyId") Long facultyId) {
        return ResponseEntity.ok(slotService.getAllSlotsByFaculty(facultyId));
    }

    /**
     * POST /api/slots
     * Create a new slot (faculty only).
     */
    @PostMapping
    public ResponseEntity<?> createSlot(
            @Valid @RequestBody AppointmentDto.SlotRequest request,
            @AuthenticationPrincipal User currentUser) {
        try {
            AppointmentDto.SlotResponse response = slotService.createSlot(currentUser.getId(), request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/slots/{slotId}
     * Delete a slot (faculty only, must own the slot).
     */
    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> deleteSlot(
            @PathVariable("slotId") Long slotId,
            @AuthenticationPrincipal User currentUser) {
        try {
            slotService.deleteSlot(slotId, currentUser.getId());
            return ResponseEntity.ok(Map.of("message", "Slot deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
