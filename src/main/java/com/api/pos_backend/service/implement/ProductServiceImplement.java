package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.ProductDTO;
import com.api.pos_backend.entity.Category;
import com.api.pos_backend.entity.Imagen;
import com.api.pos_backend.entity.Product;
import com.api.pos_backend.exception.ResourceAlreadyExistsException;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.mapper.ProductMapper;
import com.api.pos_backend.repository.CategoryRepository;
import com.api.pos_backend.repository.ProductRepository;
import com.api.pos_backend.repository.SaleDetailsRepository;
import com.api.pos_backend.service.ImagenService;
import com.api.pos_backend.service.ProductService;
import com.api.pos_backend.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImplement implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SaleDetailsRepository saleDetailsRepository;
    private final ImagenService imagenService;

    @Override
    public PageResponse<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> product = productRepository.findAll(pageable);
        Page<ProductDTO> response = product.map(ProductMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron productos",
                    404
            );
        }
        return PageResponse.fromPage(
                response,
                "Productos obtenidos exitosamente",
                200
        );
    }

    @Override
    public PageResponse<ProductDTO> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        ProductDTO response = ProductMapper.toDTO(product);
        return PageResponse.single(
                response,
                "Producto obtenido exitosamente",
                200
        );
    }

    @Override
    public PageResponse<ProductDTO> getProductByName(String name) {
        Product product = productRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con nombre: " + name));
        ProductDTO response = ProductMapper.toDTO(product);
        return PageResponse.single(
                response,
                "Producto obtenido exitosamente",
                200
        );
    }

    @Override
    public PageResponse<ProductDTO> getProductsByNameContaining(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            return PageResponse.empty(
                    null,
                    "No se encontraron productos con el nombre: " + name,
                    404
            );
        }
        List<ProductDTO> response = products.stream().map(ProductMapper::toDTO).toList();
        return PageResponse.of(
                response,
                "Productos obtenidos exitosamente",
                200
        );
    }

    @Override
    public PageResponse<ProductDTO> createProduct(ProductDTO productDTO, MultipartFile image) throws Exception {
        Category category = categoryRepository.findById(productDTO.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + productDTO.getCategory().getId()));

        boolean existsByName = productRepository.existsByName(productDTO.getName());
        if (existsByName) {
            throw new ResourceAlreadyExistsException("El producto con nombre " + productDTO.getName() + " ya existe");
        }
        Product product = ProductMapper.toEntity(productDTO);
        product.setCategory(category);
        if (image != null && !image.isEmpty()) {
            Imagen imagen = imagenService.uploadImage(image);
            product.setImagen(imagen);
        }

        Product savedProduct = productRepository.save(product);
        ProductDTO response = ProductMapper.toDTO(savedProduct);

        return PageResponse.single(
                response,
                "Producto creado exitosamente",
                201
        );
    }

    @Override
    public PageResponse<ProductDTO> updateProduct(Long id, ProductDTO productDTO, MultipartFile image) throws Exception {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        boolean existsByName = productRepository.existsByName(productDTO.getName());
        if (existsByName && !existingProduct.getName().equalsIgnoreCase(productDTO.getName())) {
            throw new ResourceAlreadyExistsException("El producto con nombre " + productDTO.getName() + " ya existe");
        }

        Category category = categoryRepository.findById(productDTO.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + productDTO.getCategory().getId()));
        Product product = ProductMapper.toEntity(productDTO);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setCategory(category);
        if (image != null && !image.isEmpty()) {
            if (existingProduct.getImagen() != null) {
                imagenService.deleteImage(existingProduct.getImagen());
            }
            Imagen imagen = imagenService.uploadImage(image);
            existingProduct.setImagen(imagen);
        }
        Product updatedProduct = productRepository.save(existingProduct);
        ProductDTO response = ProductMapper.toDTO(updatedProduct);
        return PageResponse.single(
                response,
                "Producto actualizado exitosamente",
                200
        );
    }

    @Override
    public PageResponse<ProductDTO> getProductsByCategoryName(Pageable pageable, String categoryName) {
        Page<Product> products = productRepository.findProductsByCategoryNameIgnoreCase(pageable, categoryName);
        Page<ProductDTO> response = products.map(ProductMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron productos en la categoria: " + categoryName,
                    404
            );
        }
        return PageResponse.fromPage(
                response,
                "Productos obtenidos exitosamente",
                200
        );
    }

    @Override
    public String deleteProduct(Long id) {

        boolean productInUse = saleDetailsRepository.existsByProductId(id);
        if (productInUse) {
            throw new ResourceAlreadyExistsException("El produto esta en uso no se puede eliminar");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        productRepository.delete(product);
        return "Producto eliminado exitosamente";
    }
}
