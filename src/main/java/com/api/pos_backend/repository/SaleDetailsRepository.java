package com.api.pos_backend.repository;

import com.api.pos_backend.entity.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {

    boolean existsByProductId(Long id);
}
