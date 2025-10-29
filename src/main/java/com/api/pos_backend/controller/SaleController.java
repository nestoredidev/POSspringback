package com.api.pos_backend.controller;

import com.api.pos_backend.dto.SaleDTO;
import com.api.pos_backend.service.SaleService;
import com.api.pos_backend.service.implement.SaleServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/sales")
@RequiredArgsConstructor
@Slf4j
public class SaleController {
    private final SaleServiceImplement saleService ;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<PageResponse<SaleDTO>> createSale(@Valid @RequestBody SaleDTO saleDTO) {
        try {
            PageResponse<SaleDTO> createdSale = saleService.createSale(saleDTO);
            return ResponseEntity.ok(
                    PageResponse.<SaleDTO>builder()
                            .content(createdSale.getContent())
                            .message(createdSale.getMessage())
                            .code(createdSale.getCode())
                            .build()
            );
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<SaleDTO>> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);

        PageResponse<SaleDTO> response = saleService.getAllSales(pageable);
        log.info("Se han obtenido todas las ventas. Página: {}, Tamaño: {}", page, size);
        return ResponseEntity.ok(
                PageResponse.<SaleDTO>builder()
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
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<SaleDTO>> getSaleById(@PathVariable Long id) {
        PageResponse<SaleDTO> response = saleService.getSaleById(id);
        return ResponseEntity.ok(
                PageResponse.<SaleDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PageResponse<SaleDTO>> getSalesByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort, direction);

        if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
            return ResponseEntity.badRequest().body(
                    PageResponse.<SaleDTO>builder()
                            .content(List.of())
                            .message("Debe proporcionar tanto startDate como endDate")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }

        PageResponse<SaleDTO> response = saleService.getSalesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(
                PageResponse.<SaleDTO>builder()
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
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateSalePdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = saleService.generateSalePdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "factura_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public ResponseEntity<String> deleteSale(@PathVariable Long id) {
        String response = saleService.deleteSale(id);
        return ResponseEntity.ok(response);
    }
}