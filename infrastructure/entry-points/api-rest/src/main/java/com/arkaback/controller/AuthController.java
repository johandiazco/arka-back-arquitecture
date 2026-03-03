package com.arkaback.controller;

import com.arkaback.entity.person.Person;
import com.arkaback.ports.output.PersonRepository;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Person person = personRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), person.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(person.getEmail(), "CLIENT");

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .email(person.getEmail())
                .name(person.getName())
                .role("CLIENT")
                .message("Login exitoso")
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {

        if (personRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Person person = Person.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .isActive(true)
                .build();

        Person saved = personRepository.save(person);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RegisterResponse.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .email(saved.getEmail())
                        .message("Usuario registrado exitosamente")
                        .build());
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;
    }

    @Data @Builder
    public static class LoginResponse {
        private String token;
        private String email;
        private String name;
        private String role;
        private String message;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank private String name;
        @NotBlank @Email private String email;
        @NotBlank @Size(min = 6) private String password;
        private String phone;
        private String address;
    }

    @Data @Builder @AllArgsConstructor
    public static class RegisterResponse {
        private Long id;
        private String name;
        private String email;
        private String message;
    }
}