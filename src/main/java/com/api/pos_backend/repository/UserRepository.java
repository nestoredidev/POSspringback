package com.api.pos_backend.repository;

import com.api.pos_backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);

    List<Users> findByUsernameContainingIgnoreCase(String username);


    boolean existsByRoleId(Long id);
}
