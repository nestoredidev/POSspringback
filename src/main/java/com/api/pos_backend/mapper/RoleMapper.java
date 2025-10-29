package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.RoleDTO;
import com.api.pos_backend.entity.Permission;
import com.api.pos_backend.entity.Role;

import java.util.HashSet;
import java.util.stream.Collectors;

public class RoleMapper {
    public static RoleDTO toDTO(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList()))
                .build();


    }

    public static Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) return null;
        return Role.builder()
                .id(roleDTO.getId())
                .name(roleDTO.getName())
                .permissions(new HashSet<>())
                .build();
    }
}
