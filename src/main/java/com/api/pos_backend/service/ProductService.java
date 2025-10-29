package com.api.pos_backend.service;

import com.api.pos_backend.dto.ProductDTO;
import com.api.pos_backend.shared.pagination.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ProductService {

    PageResponse<ProductDTO> getAllProducts(Pageable pageable);

    PageResponse<ProductDTO> getProductById(Long id);

    PageResponse<ProductDTO> getProductByName(String name);

    PageResponse<ProductDTO> getProductsByNameContaining(String name);

    PageResponse<ProductDTO> createProduct(ProductDTO productDTO, MultipartFile image) throws Exception;

    PageResponse<ProductDTO> updateProduct(Long id, ProductDTO productDTO, MultipartFile image) throws Exception;

    PageResponse<ProductDTO> getProductsByCategoryName(Pageable pageable, String categoryName);

    String deleteProduct(Long id);
}
