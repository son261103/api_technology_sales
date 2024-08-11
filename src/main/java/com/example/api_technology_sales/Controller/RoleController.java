package com.example.api_technology_sales.Controller;

import com.example.api_technology_sales.DTO.PermissionsDTO;
import com.example.api_technology_sales.DTO.RolesDTO;
import com.example.api_technology_sales.Exception.ResourceNotFoundException;
import com.example.api_technology_sales.Service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RolesDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolesDTO> getRoleById(@PathVariable Long id) {
        RolesDTO role = roleService.getRoleById(id);
        if (role == null) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id + " / Role not found with id: " + id);
        }
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<RolesDTO> createRole(@Valid @RequestBody RolesDTO roleDTO) {
        try {
            return ResponseEntity.ok(roleService.createRole(roleDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo vai trò: " + e.getMessage() + " / Cannot create role: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RolesDTO roleDTO) {
        try {
            roleDTO.setRoleId(id);  // Đặt ID từ path variable vào DTO
            RolesDTO updatedRole = roleService.updateRole(id, roleDTO);
            return ResponseEntity.ok(updatedRole);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id + " / Role not found with id: " + id);
        }
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        try {
            roleService.addPermissionToRole(roleId, permissionId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò hoặc quyền / Role or permission not found");
        }
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<Void> updateRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        try {
            roleService.removePermissionFromRole(roleId, permissionId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò hoặc quyền / Role or permission not found");
        }
    }

    @PutMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> updatePermissionInRole(@PathVariable Long roleId, @PathVariable Long permissionId, @RequestBody PermissionsDTO updatedPermission) {
        roleService.updatePermissionInRole(roleId, permissionId, updatedPermission);
        return ResponseEntity.ok().build();
    }
}