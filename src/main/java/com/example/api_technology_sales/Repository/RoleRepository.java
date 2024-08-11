package com.example.api_technology_sales.Repository;

import com.example.api_technology_sales.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}