package com.clinicapi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.clinicapi.dto.AppointmentDtos.AppointmentRequest;
import com.clinicapi.dto.AppointmentDtos.AppointmentResponse;
import com.clinicapi.exception.BusinessException;
import com.clinicapi.exception.ResourceNotFoundException;
import com.clinicapi.model.Appointment;
import com.clinicapi.model.AppointmentStatus;
import com.clinicapi.model.Doctor;
import com.clinicapi.model.DoctorSchedule;
import com.clinicapi.model.Patient;
import com.clinicapi.repository.AppointmentRepository;
import com.clinicapi.repository.DoctorRepository;
import com.clinicapi.repository.DoctorScheduleRepository;
import com.clinicapi.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository scheduleRepository;

    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente nao encontrado com id: " + request.patientId()));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profissional nao encontrado com id: " + request.doctorId()));

        validateDoctorAvailability(doctor, request.scheduledAt().toLocalDate(),
                request.scheduledAt().toLocalTime());

        if (appointmentRepository.existsByDoctorIdAndScheduledAt(doctor.getId(), request.scheduledAt())) {
            throw new BusinessException("Ja existe uma consulta marcada para este profissional neste horario");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .scheduledAt(request.scheduledAt())
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.notes())
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AppointmentResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public List<AppointmentResponse> findByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream().map(this::toResponse).toList();
    }

    public List<AppointmentResponse> findByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream().map(this::toResponse).toList();
    }

    public AppointmentResponse updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Nao e possivel alterar o status de uma consulta ja "
                    + appointment.getStatus().name().toLowerCase());
        }

        appointment.setStatus(newStatus);
        return toResponse(appointmentRepository.save(appointment));
    }
    private void validateDoctorAvailability(Doctor doctor, LocalDate date, LocalTime time) {
        Optional<DoctorSchedule> scheduleOpt = scheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(), date);

        DoctorSchedule schedule = scheduleOpt.orElseThrow(() -> new BusinessException(
                "Profissional nao possui escala de atendimento cadastrada para esta data"));

        if (!schedule.isAvailable()) {
            throw new BusinessException("Profissional nao esta disponivel nesta data");
        }

        if (time.isBefore(schedule.getStartTime()) || !time.isBefore(schedule.getEndTime())) {
            throw new BusinessException("Horario fora do expediente do profissional ("
                    + schedule.getStartTime() + " - " + schedule.getEndTime() + ")");
        }
    }

    private Appointment getOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta nao encontrada com id: " + id));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getFullName(),
                a.getDoctor().getId(),
                a.getDoctor().getFullName(),
                a.getScheduledAt(),
                a.getStatus(),
                a.getNotes(),
                a.getCreatedAt()
        );
    }
}
