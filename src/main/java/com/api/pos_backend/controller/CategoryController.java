package com.api.pos_backend.controller;

import com.api.pos_backend.dto.CategoryDTO;
import com.api.pos_backend.service.implement.CategoryServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServiceImplement categoryService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<CategoryDTO>> getAllCatgories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        PageResponse<CategoryDTO> response = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(
                PageResponse.<CategoryDTO>builder()
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
    public ResponseEntity<PageResponse<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        PageResponse<CategoryDTO> response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(
                PageResponse.<CategoryDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<CategoryDTO>> getCategoryByName(@PathVariable String name) {
        PageResponse<CategoryDTO> response = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(
                PageResponse.<CategoryDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<PageResponse<CategoryDTO>> createCategory(@RequestBody CategoryDTO categoryDTO) {
        PageResponse<CategoryDTO> response = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(
                PageResponse.<CategoryDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<CategoryDTO>> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        PageResponse<CategoryDTO> response = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(
                PageResponse.<CategoryDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        String response = categoryService.deleteCategory(id);
        return ResponseEntity.ok(response);
    }
}
