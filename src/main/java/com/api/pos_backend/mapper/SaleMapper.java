package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.SaleDTO;
import com.api.pos_backend.entity.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleMapper {

    private final SaleDetailsMapper saleDetailsMapper;

    public SaleDTO toDTO(Sale sale) {
        if (sale == null) return null;
        return SaleDTO.builder()
                .id(sale.getId())
                .totalAmount(sale.getTotalAmount())
                .coupon(CouponMapper.toDTO(sale.getCoupon()))
                .user(UserMapper.toDTO(sale.getUser()))
                .saleDetails(
                        sale.getSaleDetails() != null ?
                                sale.getSaleDetails().stream()
                                        .map(saleDetailsMapper::toDTO)
                                        .collect(Collectors.toList())
                                : null
                )
                .build();
    }

    public Sale toEntity(SaleDTO saleDTO) {
        if (saleDTO == null) return null;
        return Sale.builder()
                .id(saleDTO.getId())
                .totalAmount(saleDTO.getTotalAmount())
                .coupon(CouponMapper.toEntity(saleDTO.getCoupon()))
                .user(UserMapper.toEntity(saleDTO.getUser()))
                .build();
    }
}