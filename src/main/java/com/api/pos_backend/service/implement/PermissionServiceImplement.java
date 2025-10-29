package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.PermissionDTO;
import com.api.pos_backend.entity.Permission;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.mapper.PermissionMapper;
import com.api.pos_backend.repository.PermissionRepository;
import com.api.pos_backend.repository.RoleRepository;
import com.api.pos_backend.service.PermissionService;
import com.api.pos_backend.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceImplement implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository rolerepository;

    @Override
    public PageResponse<PermissionDTO> getAllPermissions(Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        Page<PermissionDTO> response = permissions.map(PermissionMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron permisos",
                    204
            );
        }
        return PageResponse.fromPage(
                response,
                "Permisos encontrados",
                200
        );
    }

    @Override
    public PageResponse<PermissionDTO> getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + id));
        PermissionDTO response = PermissionMapper.toDTO(permission);
        return PageResponse.single(
                response,
                "Permiso encontrado",
                200
        );
    }

    @Override
    public PageResponse<PermissionDTO> getPermissionByName(String name) {
        Permission permission = permissionRepository.findByNameIgnoreCase(name);
        if (permission == null) {
            return PageResponse.empty(
                    null,
                    "No se encontraron permisos con el nombre: " + name,
                    204
            );
        }
        PermissionDTO response = PermissionMapper.toDTO(permission);
        return PageResponse.single(
                response,
                "Permiso encontrado",
                200
        );
    }

    @Override
    public PageResponse<PermissionDTO> createPermission(PermissionDTO permissionDTO) {
        if (permissionRepository.existsByName(permissionDTO.getName())) {
            return PageResponse.empty(
                    null,
                    "El permiso con nombre " + permissionDTO.getName() + " ya existe",
                    400
            );
        }
        Permission permission = PermissionMapper.toEntity(permissionDTO);
        Permission savedPermission = permissionRepository.save(permission);
        PermissionDTO response = PermissionMapper.toDTO(savedPermission);
        return PageResponse.single(
                response,
                "Permiso creado",
                201
        );
    }

    @Override
    public PageResponse<PermissionDTO> updatePermission(Long id, PermissionDTO permissionDTO) {
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + id));

        if (!existingPermission.getName().equals(permissionDTO.getName()) &&
                permissionRepository.existsByName(permissionDTO.getName())) {
            return PageResponse.empty(
                    null,
                    "El permiso con nombre " + permissionDTO.getName() + " ya existe",
                    400
            );
        }

        existingPermission.setName(permissionDTO.getName());
        Permission updatedPermission = permissionRepository.save(existingPermission);
        PermissionDTO response = PermissionMapper.toDTO(updatedPermission);
        return PageResponse.single(
                response,
                "Permiso actualizado",
                200
        );
    }

    @Override
    public String deletePermission(Long id) {

        boolean permissionInUse = rolerepository.existsPermissionById(id);
        if (permissionInUse) {
            throw new ResourceNotFoundException("No se puede eliminar el permiso porque esta asignado a un rol");
        }
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + id));
        permissionRepository.delete(existingPermission);
        return permissionInUse ? "No se puede eliminar el permiso porque esta asignado a un rol" : "Permiso eliminado exitosamente";
    }
}
