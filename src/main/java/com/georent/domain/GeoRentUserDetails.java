package com.georent.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Getter
@AllArgsConstructor
public class GeoRentUserDetails implements UserDetails {

    private Long userId;

    @JsonIgnore
    private transient String email;

    @JsonIgnore
    private transient String password;

    private boolean isEnabled;

    private Collection<? extends GrantedAuthority> authorities;

    public static GeoRentUserDetails create(final GeoRentUser user) {
        final Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority()));
        return new GeoRentUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                true,
                authorities
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
