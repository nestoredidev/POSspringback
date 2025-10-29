package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.CouponDTO;
import com.api.pos_backend.entity.Coupon;

import java.time.LocalDate;

public class CouponMapper {
    public static CouponDTO toDTO(Coupon coupon) {
        if (coupon == null) return null;
        return CouponDTO.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .discount(coupon.getDiscount())
                .expirationDate(coupon.getExpirationDate().toString())
                .build();
    }

    public static Coupon toEntity(CouponDTO couponDTO) {
        if (couponDTO == null) return null;
        return Coupon.builder()
                .id(couponDTO.getId())
                .name(couponDTO.getName())
                .discount(couponDTO.getDiscount())
                .expirationDate(LocalDate.parse(couponDTO.getExpirationDate()))
                .build();
    }
}
