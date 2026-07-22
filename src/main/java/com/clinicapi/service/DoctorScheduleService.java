package com.clinicapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicapi.dto.ScheduleDtos.ScheduleRequest;
import com.clinicapi.dto.ScheduleDtos.ScheduleResponse;
import com.clinicapi.exception.BusinessException;
import com.clinicapi.exception.ResourceNotFoundException;
import com.clinicapi.model.Doctor;
import com.clinicapi.model.DoctorSchedule;
import com.clinicapi.repository.DoctorRepository;
import com.clinicapi.repository.DoctorScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleResponse create(ScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profissional nao encontrado com id: " + request.doctorId()));

        if (scheduleRepository.findByDoctorIdAndWorkDate(doctor.getId(), request.workDate()).isPresent()) {
            throw new BusinessException("Este profissional ja possui escala cadastrada para esta data");
        }

        if (!request.endTime().isAfter(request.startTime())) {
            throw new BusinessException("Horario de fim deve ser posterior ao horario de inicio");
        }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .workDate(request.workDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .available(true)
                .build();

        return toResponse(scheduleRepository.save(schedule));
    }
    public List<ScheduleResponse> findByDate(LocalDate date) {
        return scheduleRepository.findByWorkDateAndAvailableTrue(date).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ScheduleResponse> findByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::toResponse)
                .toList();
    }

    public void cancel(Long scheduleId) {
        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Escala nao encontrada com id: " + scheduleId));
        schedule.setAvailable(false);
        scheduleRepository.save(schedule);
    }

    private ScheduleResponse toResponse(DoctorSchedule s) {
        return new ScheduleResponse(s.getId(), s.getDoctor().getId(), s.getDoctor().getFullName(),
                s.getWorkDate(), s.getStartTime(), s.getEndTime(), s.isAvailable());
    }
}
