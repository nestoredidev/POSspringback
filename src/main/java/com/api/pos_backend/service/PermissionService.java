package com.api.pos_backend.service;

import com.api.pos_backend.dto.PermissionDTO;
import com.api.pos_backend.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;

public interface PermissionService {

    PageResponse<PermissionDTO> getAllPermissions(Pageable pageable);

    PageResponse<PermissionDTO> getPermissionById(Long id);

    PageResponse<PermissionDTO> getPermissionByName(String name);

    PageResponse<PermissionDTO> createPermission(PermissionDTO permissionDTO);

    PageResponse<PermissionDTO> updatePermission(Long id, PermissionDTO permissionDTO);

    String deletePermission(Long id);
}
