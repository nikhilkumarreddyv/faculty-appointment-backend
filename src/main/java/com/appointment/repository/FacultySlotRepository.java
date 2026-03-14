package com.appointment.repository;

import com.appointment.entity.FacultySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for FacultySlot entity database operations.
 */
@Repository
public interface FacultySlotRepository extends JpaRepository<FacultySlot, Long> {

    List<FacultySlot> findByFacultyIdOrderBySlotDateAscStartTimeAsc(Long facultyId);

    List<FacultySlot> findByFacultyIdAndIsAvailableTrueOrderBySlotDateAscStartTimeAsc(Long facultyId);

    List<FacultySlot> findByFacultyIdAndSlotDateBetween(Long facultyId, LocalDate from, LocalDate to);

    @Query("SELECT fs FROM FacultySlot fs WHERE fs.faculty.id = :facultyId " +
           "AND fs.slotDate >= :today AND fs.isAvailable = true ORDER BY fs.slotDate, fs.startTime")
    List<FacultySlot> findUpcomingAvailableSlotsByFaculty(@Param("facultyId") Long facultyId,
                                                           @Param("today") LocalDate today);

    @Query("SELECT COUNT(fs) FROM FacultySlot fs WHERE fs.faculty.id = :facultyId " +
           "AND fs.isAvailable = true AND fs.slotDate >= CURRENT_DATE")
    long countAvailableSlotsByFaculty(@Param("facultyId") Long facultyId);
}
