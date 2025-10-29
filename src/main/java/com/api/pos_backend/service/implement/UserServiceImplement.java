package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.UserDTO;
import com.api.pos_backend.entity.Role;
import com.api.pos_backend.entity.Users;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.mapper.UserMapper;
import com.api.pos_backend.repository.RoleRepository;
import com.api.pos_backend.repository.UserRepository;
import com.api.pos_backend.service.UserService;
import com.api.pos_backend.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<UserDTO> getAllUsers(Pageable pageable) {
        Page<Users> users = userRepository.findAll(pageable);
        Page<UserDTO> response = users.map(UserMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron usuarios",
                    204
            );
        }
        return PageResponse.fromPage(
                response,
                "Lista de usuarios",
                200
        );
    }

    @Override
    public PageResponse<UserDTO> getUserProfile(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        UserDTO response = UserMapper.toDTO(user);
        return PageResponse.single(
                response,
                "Usuario encontrado",
                200
        );
    }

    @Override
    public PageResponse<UserDTO> getUserByUsername(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
        UserDTO response = UserMapper.toDTO(user);
        return PageResponse.single(
                response,
                "Usuario encontrado",
                200
        );
    }

    @Override
    public PageResponse<UserDTO> getListByUsername(String name) {
        List<Users> users = userRepository.findByUsernameContainingIgnoreCase(name);
        if (users.isEmpty()) {
            return PageResponse.empty(
                    null,
                    "No se encontraron usuarios con el nombre: " + name,
                    204
            );
        }
        List<UserDTO> response = users.stream().map(UserMapper::toDTO).toList();
        return PageResponse.of(
                response,
                "Lista de usuarios con el nombre: " + name,
                200
        );
    }

    @Override
    public PageResponse<UserDTO> updateUser(Long id, UserDTO userDTO) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        Role existingRole = roleRepository.findById(userDTO.getRole().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + userDTO.getRole().getId()));


        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        existingUser.setRole(new HashSet<>());
        existingUser.getRole().add(existingRole);
        Users updatedUser = userRepository.save(existingUser);
        UserDTO response = UserMapper.toDTO(updatedUser);
        return PageResponse.single(
                response,
                "Usuario actualizado",
                200
        );
    }

    @Override
    public String deleteUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        userRepository.delete(user);
        return "Usuario eliminado exitosamente";
    }
}
