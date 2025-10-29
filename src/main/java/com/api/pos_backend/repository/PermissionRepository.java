package com.api.pos_backend.repository;

import com.api.pos_backend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByName(String name);

    Permission findByNameIgnoreCase(String name);

    List<Permission> findByNameIn(List<String> names);
}
