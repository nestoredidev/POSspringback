package com.api.pos_backend.controller;

import com.api.pos_backend.dto.CouponDTO;
import com.api.pos_backend.service.implement.CouponServiceImplement;
import com.api.pos_backend.shared.pagination.PageResponse;
import com.api.pos_backend.shared.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/coupons")
@RequiredArgsConstructor

public class CouponController {

    private final CouponServiceImplement couponService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CouponDTO>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction

    ) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);
        PageResponse<CouponDTO> response = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(
                PageResponse.<CouponDTO>builder()
                        .content(response.getContent())
                        .page(response.getPage())
                        .size(response.getSize())
                        .totalElements(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .hasNext(response.getHasNext())
                        .hasPrevious(response.getHasPrevious())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CouponDTO>> getCouponById(@PathVariable Long id) {
        PageResponse<CouponDTO> response = couponService.getCouponById(id);
        return ResponseEntity.ok(
                PageResponse.<CouponDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CouponDTO>> createCoupon(@RequestBody CouponDTO couponDTO) {
        PageResponse<CouponDTO> response = couponService.createCoupon(couponDTO);
        return ResponseEntity.status(response.getCode()).body(
                PageResponse.<CouponDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CouponDTO>> updateCoupon(@PathVariable Long id, @RequestBody CouponDTO couponDTO) {
        PageResponse<CouponDTO> response = couponService.updateCoupon(id, couponDTO);
        return ResponseEntity.status(response.getCode()).body(
                PageResponse.<CouponDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @PostMapping("/apply/{name}")
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<PageResponse<CouponDTO>> applyCoupon(@PathVariable String name) {
        PageResponse<CouponDTO> response = couponService.applyCoupon(name);
        return  ResponseEntity.status(response.getCode()).body(
                PageResponse.<CouponDTO>builder()
                        .content(response.getContent())
                        .message(response.getMessage())
                        .code(response.getCode())
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCoupon(@PathVariable Long id) {
        String response = couponService.deleteCoupon(id);
        return ResponseEntity.ok(response);
    }
}