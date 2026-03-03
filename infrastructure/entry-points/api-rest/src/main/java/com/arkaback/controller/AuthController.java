package com.arkaback.controller;

import com.arkaback.entity.person.PersonEntity;
import com.arkaback.repository.PersonJpaRepository;
import com.arkaback.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de Autenticación JWT
 *
 * Endpoints:
 * - POST /api/auth/login - Iniciar sesión
 * - POST /api/auth/register - Registrar nuevo usuario
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final PersonJpaRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Login - Retorna JWT token
     *
     * POST /api/auth/login
     * {
     *   "email": "admin@arka.com",
     *   "password": "admin123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // Buscar usuario por email
        PersonEntity person = personRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), person.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtService.generateToken(
                person.getEmail(),
                "CLIENT" // TODO: Obtener de person.getTipoPersona() cuando esté implementado
        );

        // Retornar respuesta
        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .email(person.getEmail())
                .name(person.getName())
                .role("CLIENT")
                .message("Login exitoso")
                .build());
    }

    /**
     * Register - Crea nuevo usuario
     *
     * POST /api/auth/register
     * {
     *   "name": "Juan Pérez",
     *   "email": "juan@example.com",
     *   "password": "password123",
     *   "phone": "555-0100",
     *   "address": "Calle 123"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        // Verificar si email ya existe
        if (personRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        PersonEntity person = PersonEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .isActive(true)
                .build();

        PersonEntity saved = personRepository.save(person);

        // Retornar respuesta
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RegisterResponse.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .email(saved.getEmail())
                        .message("Usuario registrado exitosamente")
                        .build());
    }

    // ==================== DTOs ====================

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;
    }

    @Data
    @Builder
    public static class LoginResponse {
        private String token;
        private String email;
        private String name;
        private String role;
        private String message;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;

        private String phone;
        private String address;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class RegisterResponse {
        private Long id;
        private String name;
        private String email;
        private String message;
    }
}