package com.example.api_technology_sales.Mapper;

import com.example.api_technology_sales.DTO.UsersDTO;
import com.example.api_technology_sales.Entity.Roles;
import com.example.api_technology_sales.Entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper implements EntityMapper<Users, UsersDTO> {
    @Override
    public Users toEntity(UsersDTO dto) {
        if (dto == null) {
            return null;
        }

        Users user = Users.builder()
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
        if (dto.getRoles() != null) {
            user.setRoles(dto.getRoles().stream()
                    .map(roleName -> Roles.builder().roleName(roleName).build())
                    .collect(Collectors.toSet()));
        }
        return user;
    }

    @Override
    public UsersDTO toDto(Users entity) {
        if (entity == null) {
            return null;
        }

        UsersDTO dto = UsersDTO.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        if (entity.getRoles() != null) {
            dto.setRoles(entity.getRoles().stream()
                    .map(Roles::getRoleName)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    @Override
    public List<Users> toEntity(List<UsersDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<UsersDTO> toDto(List<Users> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
