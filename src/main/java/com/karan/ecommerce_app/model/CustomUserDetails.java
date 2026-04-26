package com.karan.ecommerce_app.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @NonNull
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { // you don't track expiry yet - true
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // no lock mechanism yet - true
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // // no password expiry logic yet - true
        return true;
    }

    @Override
    public boolean isEnabled() { // user cannot authenticate if isEnabled - false
        return user.getIsActive();
    }


}
