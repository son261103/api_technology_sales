package com.example.api_technology_sales.Service;

import com.example.api_technology_sales.DTO.UsersDTO;
import com.example.api_technology_sales.DTO.auth.*;
import com.example.api_technology_sales.Entity.Users;
import com.example.api_technology_sales.Exception.ResourceNotFoundException;
import com.example.api_technology_sales.Mapper.UserMapper;
import com.example.api_technology_sales.Repository.UserRepository;
import com.example.api_technology_sales.Utils.PasswordUtil;
import com.example.api_technology_sales.Security.CustomUserDetailsService.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Users user = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String token = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            return buildAuthResponse(user, token, refreshToken);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password", e);
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(PasswordUtil.encodePassword(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleService.addDefaultRole(user);
        Users savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @Transactional
    public AuthResponse registerAdmin(RegisterAdminRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(PasswordUtil.encodePassword(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleService.addAdminRole(user);
        Users savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @Transactional
    public AuthResponse registerSuperAdmin(RegisterSuperAdminRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(PasswordUtil.encodePassword(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        roleService.addSuperAdminRole(user);
        Users savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    @Transactional
    public void resetSuperAdminPassword(ResetSuperAdminPasswordRequest request) {
        Users superAdmin = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Super Admin not found"));

        if (!roleService.hasSuperAdminRole(superAdmin)) {
            throw new IllegalArgumentException("User is not a Super Admin");
        }

        superAdmin.setPassword(PasswordUtil.encodePassword(request.getNewPassword()));
        superAdmin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(superAdmin);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        if (jwtService.isTokenValid(refreshToken, userDetails)) {
            String newToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);
            return buildAuthResponse(user, newToken, newRefreshToken);
        }
        throw new IllegalArgumentException("Invalid refresh token");
    }

    @Transactional
    public void logout(String token) {
        jwtService.invalidateToken(token);
    }

    private AuthResponse buildAuthResponse(Users user, String token, String refreshToken) {
        UsersDTO userDTO = userMapper.toDto(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .build();
    }
}