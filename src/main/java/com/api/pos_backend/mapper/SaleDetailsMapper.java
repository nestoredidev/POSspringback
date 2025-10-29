package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.SaleDetailsDTO;
import com.api.pos_backend.entity.Product;
import com.api.pos_backend.entity.SaleDetails;
import com.api.pos_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleDetailsMapper {

    private final ProductRepository productRepository;

    public SaleDetailsDTO toDTO(SaleDetails saleDetails) {
        if (saleDetails == null) return null;
        return SaleDetailsDTO.builder()
                .id(saleDetails.getId())
                .quantity(saleDetails.getQuantity())
                .priceAtSale(saleDetails.getPriceAtSale())
                .productId(saleDetails.getProduct().getId())
                .productName(saleDetails.getProduct().getName())
                .build();
    }

    public SaleDetails toEntity(SaleDetailsDTO saleDetailsDTO) {
        if (saleDetailsDTO == null) return null;

        Product product = productRepository.findById(saleDetailsDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return SaleDetails.builder()
                .id(saleDetailsDTO.getId())
                .quantity(saleDetailsDTO.getQuantity())
                .priceAtSale(saleDetailsDTO.getPriceAtSale())
                .product(product)
                .build();
    }
}