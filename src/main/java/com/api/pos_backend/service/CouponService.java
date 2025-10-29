package com.api.pos_backend.service;

import com.api.pos_backend.dto.CouponDTO;
import com.api.pos_backend.shared.pagination.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {

    PageResponse<CouponDTO> getAllCoupons(Pageable pageable);

    PageResponse<CouponDTO> getCouponByName(String name);

    PageResponse<CouponDTO> getCouponById(Long id);

    PageResponse<CouponDTO> createCoupon(CouponDTO couponDTO);

    PageResponse<CouponDTO> updateCoupon(Long id, CouponDTO couponDTO);

    PageResponse<CouponDTO> applyCoupon(String name);

    String deleteCoupon(Long id);
}
