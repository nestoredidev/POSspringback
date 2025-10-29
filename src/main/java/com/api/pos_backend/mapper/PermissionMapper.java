package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.PermissionDTO;
import com.api.pos_backend.entity.Permission;

public class PermissionMapper {

    public static PermissionDTO toDTO(Permission permission) {
        if (permission == null) return null;
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
    }

    public static  Permission toEntity(PermissionDTO permissionDTO){
        if(permissionDTO == null) return null;
        return Permission.builder()
                .id(permissionDTO.getId())
                .name(permissionDTO.getName())
                .build();
    }
}
