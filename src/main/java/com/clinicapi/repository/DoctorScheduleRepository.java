package com.clinicapi.repository;

import com.clinicapi.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByWorkDate(LocalDate workDate);

    List<DoctorSchedule> findByWorkDateAndAvailableTrue(LocalDate workDate);

    Optional<DoctorSchedule> findByDoctorIdAndWorkDate(Long doctorId, LocalDate workDate);

    List<DoctorSchedule> findByDoctorId(Long doctorId);
}
