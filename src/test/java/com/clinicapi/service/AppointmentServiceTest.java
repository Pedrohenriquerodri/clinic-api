package com.clinicapi.service;

import com.clinicapi.dto.AppointmentDtos.AppointmentRequest;
import com.clinicapi.exception.BusinessException;
import com.clinicapi.model.Doctor;
import com.clinicapi.model.DoctorSchedule;
import com.clinicapi.model.Patient;
import com.clinicapi.repository.AppointmentRepository;
import com.clinicapi.repository.DoctorRepository;
import com.clinicapi.repository.DoctorScheduleRepository;
import com.clinicapi.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private DoctorScheduleRepository scheduleRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = Patient.builder().id(1L).fullName("Joao Souza").build();
        doctor = Doctor.builder().id(1L).fullName("Dra. Ana Costa").build();
    }

    @Test
    void deveRecusarAgendamentoQuandoProfissionalNaoTemEscalaNoDia() {
        LocalDateTime dataConsulta = LocalDateTime.of(2026, 8, 10, 14, 0);
        AppointmentRequest request = new AppointmentRequest(1L, 1L, dataConsulta, null);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndWorkDate(1L, dataConsulta.toLocalDate()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("escala");
    }

    @Test
    void deveRecusarAgendamentoForaDoHorarioDeExpediente() {
        LocalDateTime dataConsulta = LocalDateTime.of(2026, 8, 10, 19, 0); // fora do expediente
        AppointmentRequest request = new AppointmentRequest(1L, 1L, dataConsulta, null);

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .workDate(dataConsulta.toLocalDate())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(18, 0))
                .available(true)
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(scheduleRepository.findByDoctorIdAndWorkDate(1L, dataConsulta.toLocalDate()))
                .thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("expediente");
    }
}
