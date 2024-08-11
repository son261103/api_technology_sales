package com.example.api_technology_sales.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsDTO {
    private Long permissionId;
    private String permissionName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
