package com.api.pos_backend.service.implement;

import com.api.pos_backend.dto.CategoryDTO;
import com.api.pos_backend.entity.Category;
import com.api.pos_backend.exception.ResourceAlreadyExistsException;
import com.api.pos_backend.exception.ResourceNotFoundException;
import com.api.pos_backend.mapper.CategoryMapper;
import com.api.pos_backend.repository.CategoryRepository;
import com.api.pos_backend.repository.ProductRepository;
import com.api.pos_backend.service.CategoryService;
import com.api.pos_backend.shared.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CategoryServiceImplement implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    @Override
    public PageResponse<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryDTO> response = categories.map(CategoryMapper::toDTO);
        if (response.getTotalElements() == 0) {
            return PageResponse.empty(
                    response,
                    "No se encontraron categorias",
                    404
            );
        }
        return PageResponse.fromPage(
                response,
                "Categorias obtenidas exitosamente",
                200
        );
    }

    @Override
    public PageResponse<CategoryDTO> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));

        CategoryDTO response = CategoryMapper.toDTO(category);

        return PageResponse.single(
                response,
                "Categoria obtenida exitosamente",
                200
        );
    }

    @Override
    public PageResponse<CategoryDTO> getCategoryByName(String name) {

        var valid = name.length() >= 3;
        if (!valid) {
            throw  new ResourceNotFoundException("La categoria tiene que tener al menos 3 caracteres");
        }
        Category category = categoryRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con nombre: " + name));
        CategoryDTO response = CategoryMapper.toDTO(category);
        return PageResponse.single(
                response,
                "Categoria obtenida exitosamente",
                200
        );
    }

    @Override
    public PageResponse<CategoryDTO> createCategory(CategoryDTO categoryDTO) {

        boolean existsByName = categoryRepository.existsByName(categoryDTO.getName());
        if (existsByName) {
            throw new ResourceAlreadyExistsException("La categoria con nombre " + categoryDTO.getName() + " ya existe");
        }
        Category category = CategoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO response = CategoryMapper.toDTO(savedCategory);

        return PageResponse.single(
                response,
                "Categoria creada exitosamente",
                201
        );
    }

    @Override
    public PageResponse<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));

        category.setName(categoryDTO.getName());

        Category updatedCategory = categoryRepository.save(category);
        CategoryDTO response = CategoryMapper.toDTO(updatedCategory);

        return PageResponse.single(
                response,
                "Categoria actualizada exitosamente",
                200
        );
    }

    @Override
    public String deleteCategory(Long id) {
        boolean categoryInUse = productRepository.existsByCategoryId(id);
        if (categoryInUse) {
            throw new ResourceAlreadyExistsException("No se puede eliminar el categoria porque esta asignado a un producto");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));
        categoryRepository.delete(category);

        return "Categoria eliminada exitosamente";
    }
}
