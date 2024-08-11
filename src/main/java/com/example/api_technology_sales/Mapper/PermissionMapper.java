package com.example.api_technology_sales.Mapper;

import com.example.api_technology_sales.DTO.PermissionsDTO;
import com.example.api_technology_sales.Entity.Permissions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionMapper implements EntityMapper<Permissions, PermissionsDTO> {

    @Override
    public Permissions toEntity(PermissionsDTO dto) {
        if (dto == null) {
            return null;
        }

        return Permissions.builder()
                .permissionId(dto.getPermissionId())
                .permissionName(dto.getPermissionName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Override
    public PermissionsDTO toDto(Permissions entity) {
        if (entity == null) {
            return null;
        }

        return PermissionsDTO.builder()
                .permissionId(entity.getPermissionId())
                .permissionName(entity.getPermissionName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<Permissions> toEntity(List<PermissionsDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<PermissionsDTO> toDto(List<Permissions> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}