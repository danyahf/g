package com.danya.auth.filter;

import com.danya.auth.JwtService;
import com.danya.security.authentication.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ErrorResponseWriter errorResponseWriter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(7);
        try {
            jwtService.validateToken(accessToken);
            String username = jwtService.extractClaim(accessToken, claims -> claims.get("username", String.class));
            List<?> roles = jwtService.extractClaim(accessToken, claims -> claims.get("roles", List.class));
            AuthUser authUser = new AuthUser(username, roles);
            request.setAttribute("authUser", authUser);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            errorResponseWriter.write(response, request, HttpStatus.UNAUTHORIZED, e.getLocalizedMessage());
        }
    }
}
