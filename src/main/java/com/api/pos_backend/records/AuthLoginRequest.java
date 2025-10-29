package com.api.pos_backend.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 6, max = 50, message = "El nombre de usuario debe tener entre 6 y 50 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password
) {

}
