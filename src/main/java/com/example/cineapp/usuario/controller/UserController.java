package com.example.cineapp.usuario.controller;

import com.example.cineapp.usuario.dto.UpdateUserRequest;
import com.example.cineapp.usuario.model.User;
import com.example.cineapp.usuario.repository.UserRepository;
import com.example.cineapp.security.JwtService;
import com.example.cineapp.usuario.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
public class UserController {


    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Listar todos los usuarios",
            description = "Solo accesible por superusuarios. Requiere JWT en cookie."
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios retornada exitosamente")
    @ApiResponse(responseCode = "401", description = "Token no encontrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        String jwt = Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jwt == null) {
            return ResponseEntity.status(401).body("Token no encontrado");
        }

        String email = jwtService.extractUsername(jwt);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !user.getIsSuperUser()) {
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        return ResponseEntity.ok(userRepository.findAll());
    }

    @Operation(
            summary = "Desactivar usuario",
            description = "Solo accesible por superusuarios. Requiere JWT en cookie."
    )
    @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente")
    @ApiResponse(responseCode = "401", description = "Token no encontrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id, HttpServletRequest request) {
        String jwt = Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jwt == null) {
            return ResponseEntity.status(401).body("Token no encontrado");
        }

        Object isSuperUserObj = jwtService.extractClaim(jwt, claims -> claims.get("isSuperUser"));
        boolean isSuperUser = isSuperUserObj instanceof Boolean && (Boolean) isSuperUserObj;

        if (!isSuperUser) {
            return ResponseEntity.status(403).body("Solo los superusuarios pueden desactivar usuarios");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        User targetUser = optionalUser.get();
        targetUser.setActive(false);
        userRepository.save(targetUser);

        return ResponseEntity.ok("Usuario desactivado correctamente");
    }

    @Operation(
            summary = "Reactivar usuario",
            description = "Solo accesible por superusuarios. Requiere JWT en cookie."
    )
    @ApiResponse(responseCode = "200", description = "Usuario reactivado correctamente")
    @ApiResponse(responseCode = "401", description = "Token no encontrado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateUser(@PathVariable Long id, HttpServletRequest request) {
        String jwt = Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jwt == null) {
            return ResponseEntity.status(401).body("Token no encontrado");
        }

        Object isSuperUserObj = jwtService.extractClaim(jwt, claims -> claims.get("isSuperUser"));
        boolean isSuperUser = isSuperUserObj instanceof Boolean && (Boolean) isSuperUserObj;

        if (!isSuperUser) {
            return ResponseEntity.status(403).body("Solo los superusuarios pueden reactivar usuarios");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        User user = optionalUser.get();
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok("Usuario reactivado correctamente");
    }

    @Operation(
            summary = "Actualizar perfil de usuario",
            description = "Solo el propio usuario puede actualizar su información. Requiere JWT en cookie."
    )
    @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente")
    @ApiResponse(responseCode = "401", description = "Token no encontrado")
    @ApiResponse(responseCode = "403", description = "No puedes editar otro usuario")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id,
                                           @RequestBody UpdateUserRequest request,
                                           HttpServletRequest httpRequest) {

        String jwt = Arrays.stream(httpRequest.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (jwt == null) {
            return ResponseEntity.status(401).body("Token no encontrado");
        }

        String email = jwtService.extractUsername(jwt);
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null || !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("No puedes editar otro usuario");
        }

        currentUser.setName(request.getName());
        currentUser.setLastname(request.getLastname());
        currentUser.setPhone(request.getPhone());

        userRepository.save(currentUser);

        return ResponseEntity.ok("Perfil actualizado correctamente");
    }
}
