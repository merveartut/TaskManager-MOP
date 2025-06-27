package com.merveartut.task_manager.security;

import com.merveartut.task_manager.enums.Role;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        System.out.println("Received Token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("No valid token found, skipping authentication.");
            chain.doFilter(request, response);
            return;
        }

        token = token.substring(7);
        System.out.println("Extracted Token: " + token);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("Token validation failed.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }

        String username = jwtUtil.extractUsername(token);
        Role role = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(userId);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Authentication set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
        chain.doFilter(request, response);
    }
}