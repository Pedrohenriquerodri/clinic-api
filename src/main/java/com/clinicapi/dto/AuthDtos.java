package com.clinicapi.dto;

import com.clinicapi.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank(message = "Nome e obrigatorio") String fullName,
            @Email(message = "Email invalido") @NotBlank String email,
            @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres") String password,
            @NotNull(message = "Perfil e obrigatorio") Role role
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            String email,
            String fullName,
            Role role
    ) {}
}
