package com.jobboard.config;

import com.jobboard.security.CustomUserDetailsService;
import com.jobboard.security.JwtAuthenticationEntryPoint;
import com.jobboard.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * API REST stateless : pas de session HTTP, pas de formulaire de login, l'identité est
 * portée par le JWT à chaque requête. D'où :
 * <ul>
 *   <li>CSRF désactivé — la protection CSRF n'a de sens que pour une authentification
 *       portée par un cookie de session envoyé automatiquement par le navigateur ;
 *       un Bearer token en en-tête Authorization n'est jamais envoyé "à l'insu" du
 *       client JS, donc pas de risque CSRF à couvrir ici ;</li>
 *   <li>SessionCreationPolicy.STATELESS — Spring Security ne crée ni ne lit de
 *       JSESSIONID ;</li>
 *   <li>{@link JwtAuthenticationFilter} inséré avant le filtre standard
 *       user/password pour peupler le contexte de sécurité à partir du token.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
        "/api/auth/register",
        "/api/auth/login",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/actuator/health",
        "/actuator/info",
        // Spring Boot passe par un forward interne vers /error (sendError, 404 sans
        // handler...) qui retraverse toute la chaîne de sécurité. En mode stateless,
        // le contexte y est vidé et le filtre JWT ne se réexécute pas (OncePerRequestFilter),
        // donc sans cette ligne CE second passage échoue toujours en 401 et masque le
        // vrai code (400/404/500) que BasicErrorController essaie de renvoyer.
        "/error"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * JwtAuthenticationFilter est un @Component (implémente Filter via OncePerRequestFilter),
     * donc Spring Boot l'enregistre par défaut comme filtre servlet global sur /* — EN PLUS
     * de son insertion explicite dans la chaîne Spring Security via addFilterBefore ci-dessus.
     *
     * Cette double exécution est silencieusement neutralisée par la garde "once per request"
     * d'OncePerRequestFilter... mais dans le mauvais sens : c'est l'exécution globale (qui a
     * lieu AVANT que SecurityContextHolderFilter ne réinitialise le contexte en mode stateless)
     * qui s'exécute réellement et pose l'authentification, laquelle est ensuite effacée par
     * Spring Security avant même d'atteindre l'AuthorizationFilter. On désactive donc
     * l'enregistrement automatique pour ne garder que celui, correctement positionné, de la
     * chaîne Spring Security.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> disableAutoRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }
}
