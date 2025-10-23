package com.danya.auth;

import com.danya.exception.InvalidTokenException;
import com.danya.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {
    private final long EXPIRATION_MS = 8000000;
    private final Key secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .toList();

        return Jwts.builder()
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secret)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateToken(String token) {
        try {
            extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            throw new InvalidTokenException("The access token is expired or not valid");
        }
    }
}
