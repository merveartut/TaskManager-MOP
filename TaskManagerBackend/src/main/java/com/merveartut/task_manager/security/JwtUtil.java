package com.merveartut.task_manager.security;

import com.merveartut.task_manager.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private final SecretKey secretKey = Keys.hmacShaKeyFor("01234567890123456789012345678901".getBytes());

    public String generateToken(String username, Role role, UUID userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name())
                .claim("userId", userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Role extractRole(String token) {
        String role = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(role);
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT Token");
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT Token");
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }
}
