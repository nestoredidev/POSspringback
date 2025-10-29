package com.api.pos_backend.repository;

import com.api.pos_backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE LOWER(r.name) = LOWER(?1)")
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT r FROM Role r WHERE LOWER(r.name) = LOWER(?1)")
    Role findByNameIgnoreCase(String name);

    List<Role> findByNameIn(List<String> names);

    List<Role> findByname(String name);

    boolean existsPermissionById(Long id);
}
