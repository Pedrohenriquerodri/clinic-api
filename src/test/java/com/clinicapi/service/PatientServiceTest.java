package com.clinicapi.service;

import com.clinicapi.dto.PatientDtos.PatientRequest;
import com.clinicapi.dto.PatientDtos.PatientResponse;
import com.clinicapi.exception.BusinessException;
import com.clinicapi.exception.ResourceNotFoundException;
import com.clinicapi.model.Patient;
import com.clinicapi.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private PatientRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new PatientRequest(
                "Maria Silva",
                "12345678900",
                LocalDate.of(1995, 5, 20),
                "11999998888",
                "maria@email.com",
                "Rua das Flores, 100"
        );
    }

    @Test
    void deveCriarPacienteComSucesso() {
        when(patientRepository.existsByCpf(validRequest.cpf())).thenReturn(false);
        when(patientRepository.existsByEmail(validRequest.email())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PatientResponse response = patientService.create(validRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.fullName()).isEqualTo("Maria Silva");
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaExiste() {
        when(patientRepository.existsByCpf(validRequest.cpf())).thenReturn(true);

        assertThatThrownBy(() -> patientService.create(validRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CPF");

        verify(patientRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoEncontrado() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
