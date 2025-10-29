package com.api.pos_backend.repository;

import com.api.pos_backend.dto.CategoryDTO;
import com.api.pos_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

   Optional<Category> findByNameIgnoreCase(String name);
   Optional<Category> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
