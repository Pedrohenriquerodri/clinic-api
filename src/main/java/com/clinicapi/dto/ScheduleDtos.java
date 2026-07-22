package com.clinicapi.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleDtos {

    public record ScheduleRequest(
            @NotNull(message = "Id do profissional e obrigatorio") Long doctorId,
            @NotNull(message = "Data e obrigatoria") LocalDate workDate,
            @NotNull(message = "Horario de inicio e obrigatorio") LocalTime startTime,
            @NotNull(message = "Horario de fim e obrigatorio") LocalTime endTime
    ) {}

    public record ScheduleResponse(
            Long id,
            Long doctorId,
            String doctorName,
            LocalDate workDate,
            LocalTime startTime,
            LocalTime endTime,
            boolean available
    ) {}
}
