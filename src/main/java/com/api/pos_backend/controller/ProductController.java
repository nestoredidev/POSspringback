package com.api.pos_backend.controller;

import com.api.pos_backend.dto.CategoryDTO;
import com.api.pos_backend.dto.ProductDTO;
import com.api.pos_backend.service.implement.ProductServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/v1/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServiceImplement productService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        PageResponse<ProductDTO> response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
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
    public ResponseEntity<PageResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        PageResponse<ProductDTO> response = productService.getProductById(id);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
                        .message(response.getMessage())
                        .content(response.getContent())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<ProductDTO>> getProductByName(@PathVariable String name) {
        PageResponse<ProductDTO> response = productService.getProductByName(name);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
                        .message(response.getMessage())
                        .content(response.getContent())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<ProductDTO>> getProductsByNameContaining(@RequestParam String name) {
        PageResponse<ProductDTO> response = productService.getProductsByNameContaining(name);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
                        .message(response.getMessage())
                        .content(response.getContent())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/category/{categoryName}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<ProductDTO>> getProductsByCategoryName(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        PageResponse<ProductDTO> response = productService.getProductsByCategoryName(pageable, categoryName);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
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


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<PageResponse<ProductDTO>> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        ProductDTO productDTO = ProductDTO.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .category(
                        CategoryDTO.builder()
                                .id(categoryId)
                                .build()
                )
                .build();

        PageResponse<ProductDTO> response = productService.createProduct(productDTO, file);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
                        .message(response.getMessage())
                        .content(response.getContent())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        ProductDTO productDTO = ProductDTO.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .category(
                        CategoryDTO.builder()
                                .id(categoryId)
                                .build()
                )
                .build();
        PageResponse<ProductDTO> response = productService.updateProduct(id, productDTO, file);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
                        .message(response.getMessage())
                        .content(response.getContent())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping("/partial-update/{id}")
    public ResponseEntity<PageResponse<ProductDTO>> partialUpdateProduct(@PathVariable Long id, @RequestParam(value = "file") MultipartFile file) throws Exception {
        PageResponse<ProductDTO> response = productService.getProductById(id);
        return ResponseEntity.ok(
                PageResponse.<ProductDTO>builder()
                        .message(response.getMessage())
                        .content(response.getContent())
                        .code(response.getCode())
                        .build()
        );

    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        String response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }
}
