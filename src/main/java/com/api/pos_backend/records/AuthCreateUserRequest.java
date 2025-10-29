package com.api.pos_backend.records;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthCreateUserRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 6, max = 50, message = "El nombre de usuario debe tener entre 6 y 50 caracteres")
        String username,

        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @Email(message = "El correo electrónico no es válido")
        String email,

        @Valid
        AuthCreateRoleRequest role

) {
}
