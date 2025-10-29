package com.api.pos_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(name = "imagen_id", unique = true)
    private String imagenId; // Cambiado a String para Cloudinary

    @Column(name = "imagen_url", unique = true)
    private String imagenUrl;
}