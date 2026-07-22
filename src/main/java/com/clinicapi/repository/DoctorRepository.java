package com.clinicapi.repository;

import com.clinicapi.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByCrm(String crm);
    boolean existsByCrm(String crm);
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
    List<Doctor> findByActiveTrue();
}
