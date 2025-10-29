package com.api.pos_backend.mapper;

import com.api.pos_backend.dto.ProductDTO;
import com.api.pos_backend.entity.Product;

import java.util.Map;

public class ProductMapper {
    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imagenUrl(product.getImagen() != null ? product.getImagen().getImagenUrl() : null)
                .category(CategoryMapper.toDTO(product.getCategory()))
                .build();
    }

    public static Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) return null;
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .category(CategoryMapper.toEntity(productDTO.getCategory()))
                .build();
    }
}
