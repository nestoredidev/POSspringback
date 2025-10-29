package com.api.pos_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
}
