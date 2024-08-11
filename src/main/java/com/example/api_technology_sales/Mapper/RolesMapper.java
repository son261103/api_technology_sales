package com.example.api_technology_sales.Mapper;

import com.example.api_technology_sales.DTO.PermissionsDTO;
import com.example.api_technology_sales.DTO.RolesDTO;
import com.example.api_technology_sales.Entity.Permissions;
import com.example.api_technology_sales.Entity.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolesMapper implements EntityMapper<Roles, RolesDTO> {

    private final PermissionMapper permissionsMapper;

    @Override
    public Roles toEntity(RolesDTO dto) {
        if (dto == null) {
            return null;
        }

        Roles role = Roles.builder()
                .roleId(dto.getRoleId())
                .roleName(dto.getRoleName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        if (dto.getPermissions() != null) {
            role.setPermissions(dto.getPermissions().stream()
                    .map(permissionsMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return role;
    }

    @Override
    public RolesDTO toDto(Roles entity) {
        if (entity == null) {
            return null;
        }

        RolesDTO dto = RolesDTO.builder()
                .roleId(entity.getRoleId())
                .roleName(entity.getRoleName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (entity.getPermissions() != null) {
            dto.setPermissions(entity.getPermissions().stream()
                    .map(permissionsMapper::toDto)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    @Override
    public List<Roles> toEntity(List<RolesDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<RolesDTO> toDto(List<Roles> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Set<Roles> toEntitySet(Set<RolesDTO> dtoSet) {
        if (dtoSet == null) {
            return null;
        }
        return dtoSet.stream().map(this::toEntity).collect(Collectors.toSet());
    }

    public Set<RolesDTO> toDtoSet(Set<Roles> entitySet) {
        if (entitySet == null) {
            return null;
        }
        return entitySet.stream().map(this::toDto).collect(Collectors.toSet());
    }
}