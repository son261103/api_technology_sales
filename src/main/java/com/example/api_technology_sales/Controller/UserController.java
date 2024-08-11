package com.example.api_technology_sales.Controller;

import com.example.api_technology_sales.DTO.UsersDTO;
import com.example.api_technology_sales.DTO.auth.ChangePasswordRequest;
import com.example.api_technology_sales.Exception.ResourceNotFoundException;
import com.example.api_technology_sales.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable Long id) {
        UsersDTO user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id + " / User not found with id: " + id);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/admin/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UsersDTO> createUser(@Valid @RequestBody UsersDTO userDTO) {
        try {
            return ResponseEntity.ok(userService.createUser(userDTO));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể tạo người dùng: " + e.getMessage() + " / Cannot create user: " + e.getMessage());
        }
    }

    @PutMapping("/api/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UsersDTO userDTO, Authentication authentication) {
        try {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            return ResponseEntity.ok(userService.updateUser(id, userDTO, isAdmin));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id + " / User not found with id: " + id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Không thể cập nhật người dùng: " + e.getMessage() + " / Cannot update user: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id + " / User not found with id: " + id);
        }
    }

    @PostMapping("/api/users/{id}/change-password")
    @PreAuthorize("#id == authentication.principal.userId")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
}