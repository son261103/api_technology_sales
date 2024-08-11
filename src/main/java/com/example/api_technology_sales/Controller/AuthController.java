package com.example.api_technology_sales.Controller;

import com.example.api_technology_sales.DTO.auth.*;
import com.example.api_technology_sales.Exception.ForbiddenException;
import com.example.api_technology_sales.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Tên đăng nhập hoặc mật khẩu không hợp lệ / Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Đăng ký thất bại: " + e.getMessage() + " / Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        try {
            AuthResponse response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Đăng ký admin thất bại: " + e.getMessage() + " / Admin registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/register/super-admin")
    public ResponseEntity<AuthResponse> registerSuperAdmin(@Valid @RequestBody RegisterSuperAdminRequest request) {
        try {
            AuthResponse response = authService.registerSuperAdmin(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Đăng ký super admin thất bại: " + e.getMessage() + " / Super admin registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/reset-super-admin-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> resetSuperAdminPassword(@Valid @RequestBody ResetSuperAdminPasswordRequest request) {
        try {
            authService.resetSuperAdminPassword(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Đặt lại mật khẩu super admin thất bại: " + e.getMessage() + " / Super admin password reset failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        try {
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Token làm mới không hợp lệ / Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String token) {
        try {
            authService.logout(token);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Đăng xuất thất bại: " + e.getMessage() + " / Logout failed: " + e.getMessage());
        }
    }
}