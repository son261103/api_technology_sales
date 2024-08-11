package com.example.api_technology_sales.Repository;

import com.example.api_technology_sales.Entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permissions, Long> {
    Optional<Permissions> findByPermissionName(String permissionName);

    boolean existsByPermissionName(String permissionName);
}