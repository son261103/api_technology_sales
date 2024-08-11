package com.example.api_technology_sales.Security;

import com.example.api_technology_sales.Entity.Users;
import com.example.api_technology_sales.Entity.Roles;
import com.example.api_technology_sales.Entity.Permissions;
import com.example.api_technology_sales.Repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(user);
    }

    @Getter
    public static class CustomUserDetails implements UserDetails {
        private final Long userId;
        private final String username;
        private final String password;
        private final String email;
        private final String firstName;
        private final String lastName;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
        private final List<SimpleGrantedAuthority> authorities;
        private final boolean accountNonExpired;
        private final boolean accountNonLocked;
        private final boolean credentialsNonExpired;
        private final boolean enabled;

        public CustomUserDetails(Users user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.email = user.getEmail();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
            this.authorities = new ArrayList<>();
            for (Roles role : user.getRoles()) {
                this.authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase()));
                for (Permissions permission : role.getPermissions()) {
                    this.authorities.add(new SimpleGrantedAuthority(permission.getPermissionName().toUpperCase()));
                }
            }
            this.accountNonExpired = user.isAccountNonExpired();
            this.accountNonLocked = user.isAccountNonLocked();
            this.credentialsNonExpired = user.isCredentialsNonExpired();
            this.enabled = user.isEnabled();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return accountNonExpired;
        }

        @Override
        public boolean isAccountNonLocked() {
            return accountNonLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return credentialsNonExpired;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }
    }
}