package com.clinicapi.dto;

import com.clinicapi.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentDtos {

    public record AppointmentRequest(
            @NotNull(message = "Id do paciente e obrigatorio") Long patientId,
            @NotNull(message = "Id do profissional e obrigatorio") Long doctorId,
            @NotNull(message = "Data/hora e obrigatoria") @Future LocalDateTime scheduledAt,
            String notes
    ) {}

    public record AppointmentResponse(
            Long id,
            Long patientId,
            String patientName,
            Long doctorId,
            String doctorName,
            LocalDateTime scheduledAt,
            AppointmentStatus status,
            String notes,
            LocalDateTime createdAt
    ) {}

    public record StatusUpdateRequest(
            @NotNull(message = "Status e obrigatorio") AppointmentStatus status
    ) {}
}
