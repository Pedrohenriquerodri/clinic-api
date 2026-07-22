package com.clinicapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicapi.dto.PatientDtos.PatientRequest;
import com.clinicapi.dto.PatientDtos.PatientResponse;
import com.clinicapi.exception.BusinessException;
import com.clinicapi.exception.ResourceNotFoundException;
import com.clinicapi.model.Patient;
import com.clinicapi.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("Ja existe um paciente cadastrado com este CPF");
        }
        if (patientRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe um paciente cadastrado com este email");
        }

        Patient patient = Patient.builder()
                .fullName(request.fullName())
                .cpf(request.cpf())
                .birthDate(request.birthDate())
                .phone(request.phone())
                .email(request.email())
                .address(request.address())
                .build();

        return toResponse(patientRepository.save(patient));
    }

    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PatientResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = getOrThrow(id);

        patient.setFullName(request.fullName());
        patient.setBirthDate(request.birthDate());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());

        return toResponse(patientRepository.save(patient));
    }

    public void delete(Long id) {
        Patient patient = getOrThrow(id);
        patientRepository.delete(patient);
    }

    private Patient getOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente nao encontrado com id: " + id));
    }

    private PatientResponse toResponse(Patient p) {
        return new PatientResponse(p.getId(), p.getFullName(), p.getCpf(), p.getBirthDate(),
                p.getPhone(), p.getEmail(), p.getAddress());
    }
}
