package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.UserDTO;
import com.api.pos_backend.entity.Users;

import java.util.HashSet;

public class UserMapper {
    public static UserDTO toDTO(Users users) {
        if (users == null) return null;
        return UserDTO.builder()
                .id(users.getId())
                .username(users.getUsername())
                .email(users.getEmail())
                .role(users.getRole().stream()
                                .findFirst().map(RoleMapper::toDTO)
                                .orElse(null))
                .build();
    }

    public  static Users toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;
        return Users.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(new HashSet<>())
                .build();
    }
}
