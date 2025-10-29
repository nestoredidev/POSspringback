package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.CouponDTO;
import com.api.pos_backend.entity.Coupon;
import com.api.pos_backend.exception.ResourceAlreadyExistsException;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.mapper.CouponMapper;
import com.api.pos_backend.repository.CouponRepository;
import com.api.pos_backend.service.CouponService;
import com.api.pos_backend.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponServiceImplement implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public PageResponse<CouponDTO> getAllCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        Page<CouponDTO> response = coupons.map(CouponMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron cupones",
                    404
            );
        }
        return PageResponse.fromPage(
                response,
                "Cupones obtenidos exitosamente",
                200
        );
    }

    @Override
    public PageResponse<CouponDTO> getCouponByName(String name) {
        return null;
    }

    @Override
    public PageResponse<CouponDTO> getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon no encontrado con id: " + id));
        CouponDTO response = CouponMapper.toDTO(coupon);
        return PageResponse.single(
                response,
                "Cupon obtenido exitosamente",
                200
        );
    }

    @Override
    public PageResponse<CouponDTO> createCoupon(CouponDTO couponDTO) {
        boolean existCupon = couponRepository.existsByName(couponDTO.getName());
        if (existCupon) {
            throw new ResourceAlreadyExistsException("Ya existe un cupon con el nombre: " + couponDTO.getName());
        }
        Coupon coupon = CouponMapper.toEntity(couponDTO);
        Coupon newCoupon = couponRepository.save(coupon);
        CouponDTO response = CouponMapper.toDTO(newCoupon);
        return PageResponse.single(
                response,
                "Cupon creado exitosamente",
                201
        );

    }

    @Override
    public PageResponse<CouponDTO> updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon no encontrado con id: " + id));

        if (!existingCoupon.getName().equals(couponDTO.getName())) {
            boolean existCupon = couponRepository.existsByName(couponDTO.getName());
            if (existCupon) {
                throw new ResourceAlreadyExistsException("Ya existe un cupon con el nombre: " + couponDTO.getName());
            }
        }
        existingCoupon.setName(couponDTO.getName());
        existingCoupon.setDiscount(couponDTO.getDiscount());
        existingCoupon.setExpirationDate(LocalDate.parse(couponDTO.getExpirationDate()));
        Coupon updatedCoupon = couponRepository.save(existingCoupon);
        CouponDTO response = CouponMapper.toDTO(updatedCoupon);
        return PageResponse.single(
                response,
                "Cupon actualizado exitosamente",
                200
        );
    }

    @Override
    public PageResponse<CouponDTO> applyCoupon(String name) {
        Coupon coupon = couponRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon no encontrado con nombre: " + name));
        if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new ResourceNotFoundException("El cupon con nombre: " + name + " ha expirado");
        }
        CouponDTO response = CouponMapper.toDTO(coupon);
        return PageResponse.single(
                response,
                "Cupon aplicado exitosamente",
                200
        );
    }


    @Override
    public String deleteCoupon(Long id) {
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon no encontrado con id: " + id));
        couponRepository.delete(existingCoupon);
        return "Cupon eliminado exitosamente";
    }
}
