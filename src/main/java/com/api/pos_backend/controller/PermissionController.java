package com.api.pos_backend.controller;

import com.api.pos_backend.dto.PermissionDTO;
import com.api.pos_backend.service.implement.PermissionServiceImplement;
import com.api.pos_backend.service.implement.RoleServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {
    private final PermissionServiceImplement permissionService;

    @GetMapping("/all")
    public ResponseEntity<PageResponse<PermissionDTO>> getAllPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
        PageResponse<PermissionDTO> response = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(
                PageResponse.<PermissionDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .totalElements(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .page(response.getPage())
                        .size(response.getSize())
                        .hasNext(response.getHasNext())
                        .hasPrevious(response.getHasPrevious())
                        .build()
        );
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PageResponse<PermissionDTO>> getPermissionById(@PathVariable Long id) {
        PageResponse<PermissionDTO> response = permissionService.getPermissionById(id);
        return ResponseEntity.ok(
                PageResponse.<PermissionDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageResponse<PermissionDTO>> getPermissionByName(@PathVariable String name) {
        PageResponse<PermissionDTO> response = permissionService.getPermissionByName(name);

        return ResponseEntity.ok(
                PageResponse.<PermissionDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<PageResponse<PermissionDTO>> createPermission(@RequestBody PermissionDTO permissionDTO) {
        PageResponse<PermissionDTO> response = permissionService.createPermission(permissionDTO);
        return ResponseEntity.status(response.getCode()).body(
                PageResponse.<PermissionDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PageResponse<PermissionDTO>> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {

        PageResponse<PermissionDTO> response = permissionService.updatePermission(id, permissionDTO);
        return ResponseEntity.status(response.getCode()).body(
                PageResponse.<PermissionDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {
        String response = permissionService.deletePermission(id);
        return ResponseEntity.ok(response);
    }
}
