package com.appointment.repository;

import com.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Appointment entity database operations.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Appointment> findByFacultyIdOrderBySlotSlotDateAscSlotStartTimeAsc(Long facultyId);

    @Query("SELECT a FROM Appointment a WHERE a.slot.id = :slotId " +
           "AND a.status NOT IN ('CANCELLED', 'REJECTED')")
    List<Appointment> findActiveAppointmentsBySlot(@Param("slotId") Long slotId);

    boolean existsBySlotIdAndStatusNotIn(Long slotId, List<Appointment.AppointmentStatus> statuses);

    @Query("SELECT a FROM Appointment a WHERE a.faculty.id = :facultyId " +
           "AND a.status = 'PENDING' ORDER BY a.createdAt DESC")
    List<Appointment> findPendingByFaculty(@Param("facultyId") Long facultyId);
}
