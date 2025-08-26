package com.example.cineapp.usuario.controller;

import com.example.cineapp.usuario.dto.AuthenticationRequest;
import com.example.cineapp.usuario.dto.AuthenticationResponse;
import com.example.cineapp.usuario.dto.PasswordRecoveryRequest;
import com.example.cineapp.usuario.dto.RegisterDTO;
import com.example.cineapp.usuario.model.User;
import com.example.cineapp.usuario.repository.UserRepository;
import com.example.cineapp.security.JwtService;
import com.example.cineapp.usuario.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login, logout y recuperación de contraseña")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente y token JWT generado")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO request) {
        // Forzar a que siempre sea un usuario normal
        User newUser = userService.registerUser(request, false);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("isSuperUser", newUser.getIsSuperUser());
        extraClaims.put("userId", newUser.getId());

        String jwtToken = jwtService.generateToken(extraClaims, newUser.getEmail());

        return ResponseEntity.ok(new AuthenticationResponse(jwtToken, newUser.getIsSuperUser()));
    }

    @Operation(summary = "Iniciar sesión de usuario")
    @ApiResponse(responseCode = "200", description = "Login exitoso con retorno de token JWT")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("isSuperUser", user.getIsSuperUser());
        extraClaims.put("userId", user.getId());

        String jwtToken = jwtService.generateToken(extraClaims, user.getEmail());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(2))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthenticationResponse(jwtToken, user.getIsSuperUser()));
    }

    @Operation(summary = "Cerrar sesión del usuario")
    @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente y cookie eliminada")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Sesión cerrada correctamente.");
    }

    @Operation(summary = "Recuperar contraseña del usuario")
    @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente")
    @ApiResponse(responseCode = "403", description = "Teléfono no coincide con el registrado")
    @ApiResponse(responseCode = "404", description = "Correo no encontrado")
    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody PasswordRecoveryRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Correo no encontrado");
        }

        User user = optionalUser.get();

        if (!user.getPhone().equals(request.getPhone())) {
            return ResponseEntity.status(403).body("Datos inválidos");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @Operation(summary = "Registrar un superusuario (solo superusuarios pueden hacerlo)")
    @ApiResponse(responseCode = "200", description = "Superusuario creado correctamente y token JWT generado")
    @ApiResponse(responseCode = "401", description = "Token no encontrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterDTO request, HttpServletRequest httpRequest) {
        // 1. Obtener JWT desde la cookie
        String jwt = Arrays.stream(Optional.ofNullable(httpRequest.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jwt == null) {
            return ResponseEntity.status(401).body("Token no encontrado");
        }

        // 2. Obtener usuario actual
        String email = jwtService.extractUsername(jwt);
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !currentUser.getIsSuperUser()) {
            return ResponseEntity.status(403).body("Acceso denegado: solo superusuarios pueden crear otros admins");
        }

        // 3. Crear nuevo superusuario
        User newUser = userService.registerUser(request, true);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("isSuperUser", newUser.getIsSuperUser());
        extraClaims.put("userId", newUser.getId());

        String jwtToken = jwtService.generateToken(extraClaims, newUser.getEmail());

        return ResponseEntity.ok(new AuthenticationResponse(jwtToken, newUser.getIsSuperUser()));
    }

}
