package com.api.pos_backend.controller;

import com.api.pos_backend.dto.RoleDTO;
import com.api.pos_backend.service.implement.RoleServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleServiceImplement roleService;

    @GetMapping("/all")
    public ResponseEntity<PageResponse<RoleDTO>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);
        PageResponse<RoleDTO> response = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(
                PageResponse.<RoleDTO>builder()
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
    public ResponseEntity<PageResponse<RoleDTO>> getRoleById(@PathVariable Long id) {
        PageResponse<RoleDTO> response = roleService.getRoleById(id);
        return ResponseEntity.ok(
                PageResponse.<RoleDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageResponse<RoleDTO>> getRoleByName(@PathVariable String name) {
        PageResponse<RoleDTO> response = roleService.getRoleByName(name);
        return ResponseEntity.ok(
                PageResponse.<RoleDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<PageResponse<RoleDTO>> createRole(@RequestBody RoleDTO roleDTO) {
        PageResponse<RoleDTO> response = roleService.createRole(roleDTO);
        return ResponseEntity.status(response.getCode()).body(
                PageResponse.<RoleDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PageResponse<RoleDTO>> updateRole(@Valid @PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        PageResponse<RoleDTO> response = roleService.updateRole(id, roleDTO);
        return ResponseEntity.status(response.getCode()).body(
                PageResponse.<RoleDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        String response = roleService.deleteRole(id);
        return ResponseEntity.ok(response);
    }
}
