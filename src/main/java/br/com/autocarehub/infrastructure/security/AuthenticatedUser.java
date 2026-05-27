package br.com.autocarehub.infrastructure.security;

import br.com.autocarehub.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

    private final User user;

    public AuthenticatedUser(User user) {
        this.user = user;
    }

    public UUID id() {
        return user.id();
    }

    public UUID customerId() {
        return user.customerId();
    }

    public String role() {
        return user.role().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()));
    }

    @Override
    public String getPassword() {
        return user.passwordHash();
    }

    @Override
    public String getUsername() {
        return user.username();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.active();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.active();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.active();
    }

    @Override
    public boolean isEnabled() {
        return user.active();
    }
}
