package com.example.api_technology_sales.Service;

import com.example.api_technology_sales.DTO.PermissionsDTO;
import com.example.api_technology_sales.Entity.Permissions;
import com.example.api_technology_sales.Mapper.PermissionMapper;
import com.example.api_technology_sales.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public List<PermissionsDTO> getAllPermissions() {
        List<Permissions> permissions = permissionRepository.findAll();
        return permissionMapper.toDto(permissions);
    }

    @Transactional(readOnly = true)
    public PermissionsDTO getPermissionById(Long id) {
        Permissions permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return permissionMapper.toDto(permission);
    }

    @Transactional
    public PermissionsDTO createPermission(PermissionsDTO permissionDTO) {
        if (permissionRepository.existsByPermissionName(permissionDTO.getPermissionName())) {
            throw new RuntimeException("Permission name already exists");
        }
        Permissions permission = permissionMapper.toEntity(permissionDTO);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        Permissions savedPermission = permissionRepository.save(permission);
        return permissionMapper.toDto(savedPermission);
    }

    @Transactional
    public PermissionsDTO updatePermission(Long id, PermissionsDTO permissionDTO) {
        Permissions existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (!existingPermission.getPermissionName().equals(permissionDTO.getPermissionName())
                && permissionRepository.existsByPermissionName(permissionDTO.getPermissionName())) {
            throw new RuntimeException("Permission name already exists");
        }

        existingPermission.setPermissionName(permissionDTO.getPermissionName());
        existingPermission.setDescription(permissionDTO.getDescription());
        existingPermission.setUpdatedAt(LocalDateTime.now());

        Permissions updatedPermission = permissionRepository.save(existingPermission);
        return permissionMapper.toDto(updatedPermission);
    }

    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found");
        }
        permissionRepository.deleteById(id);
    }
}
