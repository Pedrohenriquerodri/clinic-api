package com.clinicapi.service;

import com.clinicapi.dto.DoctorDtos.DoctorRequest;
import com.clinicapi.dto.DoctorDtos.DoctorResponse;
import com.clinicapi.exception.BusinessException;
import com.clinicapi.exception.ResourceNotFoundException;
import com.clinicapi.model.Doctor;
import com.clinicapi.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorResponse create(DoctorRequest request) {
        if (doctorRepository.existsByCrm(request.crm())) {
            throw new BusinessException("Ja existe um profissional cadastrado com este CRM");
        }

        Doctor doctor = Doctor.builder()
                .fullName(request.fullName())
                .crm(request.crm())
                .specialty(request.specialty())
                .phone(request.phone())
                .email(request.email())
                .active(true)
                .build();

        return toResponse(doctorRepository.save(doctor));
    }

    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<DoctorResponse> findActive() {
        return doctorRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    public DoctorResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public DoctorResponse update(Long id, DoctorRequest request) {
        Doctor doctor = getOrThrow(id);

        doctor.setFullName(request.fullName());
        doctor.setSpecialty(request.specialty());
        doctor.setPhone(request.phone());
        doctor.setEmail(request.email());

        return toResponse(doctorRepository.save(doctor));
    }

    public void deactivate(Long id) {
        Doctor doctor = getOrThrow(id);
        doctor.setActive(false);
        doctorRepository.save(doctor);
    }

    private Doctor getOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional nao encontrado com id: " + id));
    }

    private DoctorResponse toResponse(Doctor d) {
        return new DoctorResponse(d.getId(), d.getFullName(), d.getCrm(), d.getSpecialty(),
                d.getPhone(), d.getEmail(), d.isActive());
    }
}
