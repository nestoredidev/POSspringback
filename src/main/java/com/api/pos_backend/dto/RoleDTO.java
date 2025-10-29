package com.api.pos_backend.dto;

import com.api.pos_backend.entity.Permission;
import com.api.pos_backend.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class RoleDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private RoleEnum name;

    private List<String> permissions;
}
