package com.appointment.service;

import com.appointment.dto.AppointmentDto;
import com.appointment.entity.Appointment;
import com.appointment.entity.FacultySlot;
import com.appointment.entity.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.FacultySlotRepository;
import com.appointment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing appointments - booking, cancellation, and status updates.
 */
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private FacultySlotRepository slotRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Book an appointment for a student.
     * Checks slot availability before confirming.
     */
    @Transactional
    public AppointmentDto.AppointmentResponse bookAppointment(Long studentId,
                                                               AppointmentDto.BookingRequest request) {
        // Validate student
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Validate faculty
        User faculty = userRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        // Validate slot
        FacultySlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // Check slot availability
        if (!slot.getIsAvailable()) {
            throw new RuntimeException("Slot Unavailable - this time slot is already booked");
        }

        // Check if slot belongs to specified faculty
        if (!slot.getFaculty().getId().equals(request.getFacultyId())) {
            throw new RuntimeException("Slot does not belong to specified faculty");
        }

        // Check for existing active appointments on this slot
        List<Appointment.AppointmentStatus> cancelledStatuses = List.of(
                Appointment.AppointmentStatus.CANCELLED,
                Appointment.AppointmentStatus.REJECTED
        );
        boolean slotTaken = appointmentRepository.existsBySlotIdAndStatusNotIn(slot.getId(), cancelledStatuses);
        if (slotTaken) {
            throw new RuntimeException("Slot Unavailable - appointment already exists for this slot");
        }

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setStudent(student);
        appointment.setFaculty(faculty);
        appointment.setSlot(slot);
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointment.setPurpose(request.getPurpose());
        appointment.setStudentNotes(request.getStudentNotes());

        // Mark slot as unavailable
        slot.setIsAvailable(false);
        slotRepository.save(slot);

        Appointment saved = appointmentRepository.save(appointment);
        return toAppointmentResponse(saved);
    }

    /**
     * Get all appointments for a student.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto.AppointmentResponse> getStudentAppointments(Long studentId) {
        List<Appointment> appointments = appointmentRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        List<AppointmentDto.AppointmentResponse> responseList = new ArrayList<>();
        for (Appointment a : appointments) {
            responseList.add(toAppointmentResponse(a));
        }
        return responseList;
    }

    /**
     * Get all appointments for a faculty member.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto.AppointmentResponse> getFacultyAppointments(Long facultyId) {
        List<Appointment> appointments = appointmentRepository.findByFacultyIdOrderBySlotSlotDateAscSlotStartTimeAsc(facultyId);
        List<AppointmentDto.AppointmentResponse> responseList = new ArrayList<>();
        for (Appointment a : appointments) {
            responseList.add(toAppointmentResponse(a));
        }
        return responseList;
    }

    /**
     * Cancel an appointment (by student or faculty).
     */
    @Transactional
    public AppointmentDto.AppointmentResponse cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Only the student or faculty involved can cancel
        boolean isStudent = appointment.getStudent().getId().equals(userId);
        boolean isFaculty = appointment.getFaculty().getId().equals(userId);

        if (!isStudent && !isFaculty) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        // Release the slot back to available
        FacultySlot slot = appointment.getSlot();
        slot.setIsAvailable(true);
        slotRepository.save(slot);

        Appointment updated = appointmentRepository.save(appointment);
        return toAppointmentResponse(updated);
    }

    /**
     * Update appointment status (faculty accepts/rejects).
     */
    @Transactional
    public AppointmentDto.AppointmentResponse updateAppointmentStatus(Long appointmentId,
                                                                       Long facultyId,
                                                                       AppointmentDto.StatusUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to update this appointment");
        }

        appointment.setStatus(request.getStatus());
        if (request.getFacultyNotes() != null) {
            appointment.setFacultyNotes(request.getFacultyNotes());
        }

        // If rejected, release the slot
        if (request.getStatus() == Appointment.AppointmentStatus.REJECTED) {
            FacultySlot slot = appointment.getSlot();
            slot.setIsAvailable(true);
            slotRepository.save(slot);
        }

        Appointment updated = appointmentRepository.save(appointment);
        return toAppointmentResponse(updated);
    }

    /**
     * Get all appointments (admin only).
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto.AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<AppointmentDto.AppointmentResponse> responseList = new ArrayList<>();
        for (Appointment a : appointments) {
            responseList.add(toAppointmentResponse(a));
        }
        return responseList;
    }

    /**
     * Convert Appointment entity to AppointmentResponse DTO.
     */
    public AppointmentDto.AppointmentResponse toAppointmentResponse(Appointment a) {
        FacultySlot slot = a.getSlot();
        return new AppointmentDto.AppointmentResponse(
                a.getId(),
                a.getStudent().getId(),
                a.getStudent().getName(),
                a.getStudent().getEmail(),
                a.getFaculty().getId(),
                a.getFaculty().getName(),
                a.getFaculty().getDepartment(),
                slot.getId(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                a.getStatus().name(),
                a.getPurpose(),
                a.getStudentNotes(),
                a.getFacultyNotes(),
                a.getCreatedAt() != null ? a.getCreatedAt().toString() : null
        );
    }
}
