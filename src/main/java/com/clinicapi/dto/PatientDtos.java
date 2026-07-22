package com.clinicapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public class PatientDtos {

    public record PatientRequest(
            @NotBlank(message = "Nome e obrigatorio") String fullName,
            @NotBlank(message = "CPF e obrigatorio") String cpf,
            @Past(message = "Data de nascimento deve ser no passado") LocalDate birthDate,
            @NotBlank(message = "Telefone e obrigatorio") String phone,
            @Email(message = "Email invalido") @NotBlank String email,
            String address
    ) {}

    public record PatientResponse(
            Long id,
            String fullName,
            String cpf,
            LocalDate birthDate,
            String phone,
            String email,
            String address
    ) {}
}
