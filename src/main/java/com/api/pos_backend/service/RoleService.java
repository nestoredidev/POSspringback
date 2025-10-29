package com.api.pos_backend.service;

import com.api.pos_backend.dto.RoleDTO;
import com.api.pos_backend.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    PageResponse<RoleDTO> getAllRoles(Pageable pageable);

    PageResponse<RoleDTO> getRoleById(Long id);

    PageResponse<RoleDTO> getRoleByName(String name);

    PageResponse<RoleDTO> createRole(RoleDTO roleDTO);

    PageResponse<RoleDTO> updateRole(Long id, RoleDTO roleDTO);

    String deleteRole(Long id);
}
