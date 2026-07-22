package com.clinicapi.repository;

import com.clinicapi.model.Appointment;
import com.clinicapi.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByDoctorIdAndScheduledAtBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByStatus(AppointmentStatus status);

    boolean existsByDoctorIdAndScheduledAt(Long doctorId, LocalDateTime scheduledAt);
}
