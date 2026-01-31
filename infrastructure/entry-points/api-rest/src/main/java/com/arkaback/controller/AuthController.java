package com.arkaback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final PersonJpaRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        PersonEntity person = personRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), person.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(
                person.getEmail(),
                person.getTipoPersona().name()
        );

        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .email(person.getEmail())
                .role(person.getTipoPersona().name())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        if (personRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        PersonEntity person = PersonEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .tipoPersona(TipoPersona.CLIENT)  // Por defecto CLIENT
                .isActive(true)
                .build();

        personRepository.save(person);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario registrado exitosamente");
    }
}
