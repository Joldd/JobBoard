package com.jobboard.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lit l'en-tête {@code Authorization: Bearer <token>}, et si le token est valide,
 * peuple le SecurityContext pour la durée de la requête. Placé avant
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationFilter}
 * dans la chaîne (voir SecurityConfig).
 *
 * <p>Un token absent, malformé ou expiré n'est jamais une erreur ici : le filtre laisse
 * simplement la requête repartir non authentifiée, et c'est authorizeHttpRequests qui
 * décidera plus loin si la route exige d'être authentifié (→ 401 via le point d'entrée).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            String email = jwtService.extractEmail(token);
            boolean notYetAuthenticated = SecurityContextHolder.getContext().getAuthentication() == null;

            if (email != null && notYetAuthenticated) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails)) {
                    var authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            // Token malformé/expiré/signature invalide : on continue non authentifié.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
