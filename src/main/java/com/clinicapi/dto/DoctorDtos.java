package com.clinicapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class DoctorDtos {

    public record DoctorRequest(
            @NotBlank(message = "Nome e obrigatorio") String fullName,
            @NotBlank(message = "CRM e obrigatorio") String crm,
            @NotBlank(message = "Especialidade e obrigatoria") String specialty,
            @NotBlank(message = "Telefone e obrigatorio") String phone,
            @Email(message = "Email invalido") @NotBlank String email
    ) {}

    public record DoctorResponse(
            Long id,
            String fullName,
            String crm,
            String specialty,
            String phone,
            String email,
            boolean active
    ) {}
}
