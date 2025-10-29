package com.api.pos_backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImagenDTO {
    private Long id;
    private String name;
    private String imagenId;
    private String imagenUrl;
}
