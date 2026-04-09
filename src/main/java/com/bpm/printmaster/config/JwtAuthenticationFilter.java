package com.bpm.printmaster.config;

import com.bpm.printmaster.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

                 String path = request.getServletPath();
    if (path.startsWith("/auth/")) {
        filterChain.doFilter(request, response);
        return;
    }
        // 1. Leer header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token o no empieza con Bearer, continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar "Bearer ")
        final String token = authHeader.substring(7);
        final String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Token malformado
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Si hay username y no hay autenticación previa en el contexto
        if (username != null && 
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = 
                userDetailsService.loadUserByUsername(username);

            // 5. Validar token
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 6. Registrar autenticación en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}