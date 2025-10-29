package com.api.pos_backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductDTO {

    private Long id;

    @NotBlank(message = "EL nombre es obligatorio")
    private String name;

    @Size(max =500, message = "La descripción no puede exceder los 500 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    private Integer stock;

    private CategoryDTO category;

    private  String imagenUrl;
}
