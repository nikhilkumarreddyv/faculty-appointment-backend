package com.appointment.service;

import com.appointment.dto.AppointmentDto;
import com.appointment.entity.FacultySlot;
import com.appointment.entity.User;
import com.appointment.repository.FacultySlotRepository;
import com.appointment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing faculty time slots.
 */
@Service
public class SlotService {

    @Autowired
    private FacultySlotRepository slotRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new time slot for a faculty member.
     */
    @Transactional
    public AppointmentDto.SlotResponse createSlot(Long facultyId, AppointmentDto.SlotRequest request) {
        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));

        if (faculty.getRole() != User.Role.FACULTY) {
            throw new RuntimeException("User is not a faculty member");
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }

        if (request.getSlotDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot create slot for a past date");
        }

        FacultySlot slot = new FacultySlot();
        slot.setFaculty(faculty);
        slot.setSlotDate(request.getSlotDate());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setNotes(request.getNotes());
        slot.setMaxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 1);
        slot.setIsAvailable(true);

        FacultySlot saved = slotRepository.save(slot);
        return toSlotResponse(saved);
    }

    /**
     * Get all available slots for a specific faculty member.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto.SlotResponse> getAvailableSlotsByFaculty(Long facultyId) {
        List<FacultySlot> slots = slotRepository.findUpcomingAvailableSlotsByFaculty(facultyId, LocalDate.now());
        List<AppointmentDto.SlotResponse> responseList = new ArrayList<>();
        for (FacultySlot slot : slots) {
            responseList.add(toSlotResponse(slot));
        }
        return responseList;
    }

    /**
     * Get all slots (available and booked) for a faculty member.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto.SlotResponse> getAllSlotsByFaculty(Long facultyId) {
        List<FacultySlot> slots = slotRepository.findByFacultyIdOrderBySlotDateAscStartTimeAsc(facultyId);
        List<AppointmentDto.SlotResponse> responseList = new ArrayList<>();
        for (FacultySlot slot : slots) {
            responseList.add(toSlotResponse(slot));
        }
        return responseList;
    }

    /**
     * Delete a slot (only if no active appointments).
     */
    @Transactional
    public void deleteSlot(Long slotId, Long facultyId) {
        FacultySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("You can only delete your own slots");
        }

        slotRepository.delete(slot);
    }

    /**
     * Get all faculty members with their available slot counts.
     */
    public List<AppointmentDto.FacultyResponse> getAllFacultyWithSlots() {
        List<User> facultyList = userRepository.findByRole(User.Role.FACULTY);
        List<AppointmentDto.FacultyResponse> responseList = new ArrayList<>();
        for (User f : facultyList) {
            responseList.add(new AppointmentDto.FacultyResponse(
                    f.getId(),
                    f.getName(),
                    f.getEmail(),
                    f.getDepartment(),
                    f.getPhone(),
                    slotRepository.countAvailableSlotsByFaculty(f.getId())
            ));
        }
        return responseList;
    }

    /**
     * Convert FacultySlot entity to SlotResponse DTO.
     */
    public AppointmentDto.SlotResponse toSlotResponse(FacultySlot slot) {
        return new AppointmentDto.SlotResponse(
                slot.getId(),
                slot.getFaculty().getId(),
                slot.getFaculty().getName(),
                slot.getFaculty().getDepartment(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getIsAvailable(),
                slot.getNotes()
        );
    }
}
