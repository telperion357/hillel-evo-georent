package com.georent.domain;


import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return description;
    }

}