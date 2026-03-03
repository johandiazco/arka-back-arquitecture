package com.arkaback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Bean para encriptar contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Configuración de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Deshabilitado para API REST
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sin sesiones
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll() // Login y registro públicos
                .requestMatchers("/actuator/**").permitAll() // Health checks públicos
                .anyRequest().permitAll(); // TODO: Cambiar a .authenticated() en producción

        return http.build();
    }
}

/*
 * Para seguridad completa
 * JwtAuthenticationFilter:
 * @Component
 * public class JwtAuthenticationFilter extends OncePerRequestFilter {
 *     @Override
 *     protected void doFilterInternal(HttpServletRequest request,
 *                                     HttpServletResponse response,
 *                                     FilterChain filterChain) {
 *         String token = extractToken(request);
 *         if (token != null && jwtService.validateToken(token)) {
 *             // Autenticar usuario
 *         }
 *         filterChain.doFilter(request, response);
 *     }
 * }
 *
 * Cambiar .anyRequest().permitAll() a: .anyRequest().authenticated()
 *
 * Agregar los roles:
 *    .requestMatchers("/api/admin/**").hasRole("ADMIN")
 *    .requestMatchers("/api/products/**").hasAnyRole("ADMIN", "EMPLOYEE")
 */
