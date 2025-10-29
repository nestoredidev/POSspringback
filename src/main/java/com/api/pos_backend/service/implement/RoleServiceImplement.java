package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.PermissionDTO;
import com.api.pos_backend.dto.RoleDTO;
import com.api.pos_backend.entity.Permission;
import com.api.pos_backend.entity.Role;
import com.api.pos_backend.exception.ResourceAlreadyExistsException;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.mapper.RoleMapper;
import com.api.pos_backend.repository.PermissionRepository;
import com.api.pos_backend.repository.RoleRepository;
import com.api.pos_backend.repository.UserRepository;
import com.api.pos_backend.service.RoleService;
import com.api.pos_backend.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImplement implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<RoleDTO> getAllRoles(Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(pageable);
        Page<RoleDTO> response = roles.map(RoleMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron roles",
                    204
            );
        }
        return PageResponse.fromPage(
                response,
                "Lista de roles",
                200
        );
    }

    @Override
    public PageResponse<RoleDTO> getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));

        RoleDTO response = RoleMapper.toDTO(role);
        return PageResponse.single(
                response,
                "Rol encontrado",
                200
        );
    }

    @Override
    public PageResponse<RoleDTO> getRoleByName(String name) {
        Role role = roleRepository.findByNameIgnoreCase(name);
        if (role == null) {
            throw new ResourceNotFoundException("Rol no encontrado con nombre: " + name);
        }
        RoleDTO response = RoleMapper.toDTO(role);
        return PageResponse.single(
                response,
                "Rol encontrado",
                200
        );
    }

    @Override
    public PageResponse<RoleDTO> createRole(RoleDTO roleDTO) {
        boolean existRole = roleRepository.existsByNameIgnoreCase(roleDTO.getName().name());
        if (existRole) {
            throw new ResourceNotFoundException("El rol con nombre: " + roleDTO.getName().name() + " ya existe");
        }
        List<Permission> permissions = permissionRepository.findByNameIn(roleDTO.getPermissions());

        if (permissions.size() != roleDTO.getPermissions().size()) {
            throw new ResourceNotFoundException("Algunos de los permisos no existen");
        }
        Role newRole = Role.builder()
                .name(roleDTO.getName())
                .permissions(new HashSet<>(permissions))
                .build();
        Role savedRole = roleRepository.save(newRole);
        RoleDTO response = RoleMapper.toDTO(savedRole);
        return PageResponse.single(
                response,
                "Rol creado",
                201
        );

    }

    @Override
    public PageResponse<RoleDTO> updateRole(Long id, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        List<Permission> permissions = permissionRepository.findByNameIn(roleDTO.getPermissions());
        if (permissions.size() != roleDTO.getPermissions().size()) {
            throw new ResourceNotFoundException("Algunos de los permisos no existen");
        }
        existingRole.setName(roleDTO.getName());
        existingRole.setPermissions(new HashSet<>(permissions));
        Role updatedRole = roleRepository.save(existingRole);
        RoleDTO response = RoleMapper.toDTO(updatedRole);
        return PageResponse.single(
                response,
                "Rol actualizado",
                200
        );
    }

    @Override
    public String deleteRole(Long id) {
        boolean roleInUse = userRepository.existsByRoleId(id);
        if (roleInUse) {
            throw new ResourceAlreadyExistsException("No se puede eliminar el rol porque esta asignado a un usuario");
        }
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        roleRepository.delete(existingRole);
        return "Rol eliminado exitosamente";
    }
}
