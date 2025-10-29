package com.api.pos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CouponDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El descuento es obligatorio")
    private Double discount;

    @NotBlank(message = "La fecha de expiración es obligatoria")
    private String expirationDate;
}

