package com.example.api_technology_sales.Service;

import com.example.api_technology_sales.DTO.PermissionsDTO;
import com.example.api_technology_sales.DTO.RolesDTO;
import com.example.api_technology_sales.Entity.Permissions;
import com.example.api_technology_sales.Entity.Roles;
import com.example.api_technology_sales.Entity.Users;
import com.example.api_technology_sales.Exception.ResourceNotFoundException;
import com.example.api_technology_sales.Mapper.PermissionMapper;
import com.example.api_technology_sales.Mapper.RolesMapper;
import com.example.api_technology_sales.Repository.PermissionRepository;
import com.example.api_technology_sales.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolesMapper rolesMapper;
    private final PermissionMapper permissionMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RolesDTO getRoleById(Long id) {
        Roles role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò / Role not found"));
        return rolesMapper.toDto(role);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RolesDTO getRoleByName(String name) {
        Roles role = roleRepository.findByRoleName(name)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò / Role not found"));
        return rolesMapper.toDto(role);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<RolesDTO> getAllRoles() {
        List<Roles> roles = roleRepository.findAll();
        return rolesMapper.toDto(roles);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public RolesDTO createRole(RolesDTO roleDTO) {
        if (roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new RuntimeException("Tên vai trò đã tồn tại / Role name already exists");
        }
        Roles role = rolesMapper.toEntity(roleDTO);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        Roles savedRole = roleRepository.save(role);
        return rolesMapper.toDto(savedRole);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public RolesDTO updateRole(Long id, RolesDTO roleDTO) {
        Roles existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + id));

        if (!existingRole.getRoleName().equals(roleDTO.getRoleName())
                && roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại");
        }

        existingRole.setRoleName(roleDTO.getRoleName());
        existingRole.setDescription(roleDTO.getDescription());
        existingRole.setUpdatedAt(LocalDateTime.now());

        // Cập nhật permissions
        if (roleDTO.getPermissions() != null) {
            Set<Permissions> newPermissions = roleDTO.getPermissions().stream()
                    .map(permDTO -> permissionRepository.findById(permDTO.getPermissionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền với id: " + permDTO.getPermissionId())))
                    .collect(Collectors.toSet());

            // Xóa tất cả permissions hiện tại
            existingRole.getPermissions().clear();
            // Thêm permissions mới
            existingRole.getPermissions().addAll(newPermissions);
        }

        Roles savedRole = roleRepository.save(existingRole);
        return rolesMapper.toDto(savedRole);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy vai trò / Role not found");
        }
        roleRepository.deleteById(id);
    }

    @Transactional
    public void addDefaultRole(Users user) {
        Roles defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò mặc định / Default role not found"));
        user.getRoles().add(defaultRole);
    }

    @Transactional
    public void addAdminRole(Users user) {
        Roles adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò Admin / Admin role not found"));
        user.getRoles().add(adminRole);
    }

    @Transactional
    public void addSuperAdminRole(Users user) {
        Roles superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò Super Admin / Super Admin role not found"));
        user.getRoles().add(superAdminRole);
    }

    public boolean hasSuperAdminRole(Users user) {
        return user.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()));
    }

    public Optional<Roles> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void addPermissionToRole(Long roleId, Long permissionId) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + roleId));
        Permissions permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền với id: " + permissionId));

        if (role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException("Quyền đã tồn tại trong vai trò này");
        }

        role.getPermissions().add(permission);
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.save(role);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với id: " + roleId));
        Permissions permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền với id: " + permissionId));

        if (!role.getPermissions().contains(permission)) {
            throw new IllegalArgumentException("Quyền không tồn tại trong vai trò này");
        }

        role.getPermissions().remove(permission);
        role.setUpdatedAt(LocalDateTime.now());
        roleRepository.save(role);
    }

    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<Permissions> newPermissions = new HashSet<>(permissionRepository.findAllById(permissionIds));

        role.setPermissions(newPermissions);
        roleRepository.save(role);
    }

    public void updatePermissionInRole(Long roleId, Long permissionId, PermissionsDTO updatedPermission) {
        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Permissions permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        permission.setPermissionName(updatedPermission.getPermissionName());
        permission.setDescription(updatedPermission.getDescription());

        permissionRepository.save(permission);
    }
}