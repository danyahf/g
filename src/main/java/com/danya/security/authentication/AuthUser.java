package com.danya.security.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthUser {
    private final String username;
    private final List<?> roles;
}
