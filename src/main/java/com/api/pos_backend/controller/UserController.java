package com.api.pos_backend.controller;

import com.api.pos_backend.dto.UserDTO;
import com.api.pos_backend.service.implement.UserServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImplement userService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id") String sortBy,
                                                             @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        PageResponse<UserDTO> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(
                PageResponse.<UserDTO>builder()
                        .code(response.getCode())
                        .message(response.getMessage())
                        .content(response.getContent())
                        .page(response.getPage())
                        .size(response.getSize())
                        .totalElements(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .hasNext(response.getHasNext())
                        .hasPrevious(response.getHasPrevious())
                        .build()
        );

    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<UserDTO>> getUserProfile(@PathVariable Long id) {
        PageResponse<UserDTO> response = userService.getUserProfile(id);
        return ResponseEntity.ok(
                PageResponse.<UserDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserDTO>> updateUser(@Valid @PathVariable Long id, @RequestBody UserDTO userDTO) {
        PageResponse<UserDTO> response = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(
                PageResponse.<UserDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }
}
