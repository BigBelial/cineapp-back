package com.example.cineapp.usuario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un usuario del sistema CineApp")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastname;

    @Column(unique = true)
    @Schema(description = "Correo electrónico único del usuario", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Número de teléfono del usuario", example = "+573001234567")
    private String phone;

    @Schema(description = "Contraseña encriptada del usuario", example = "$2a$10$...")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Indica si el usuario está activo", example = "true")
    private Boolean active = true;

    @Column(name = "is_superuser")
    @Schema(description = "Indica si el usuario tiene privilegios de superusuario", example = "false")
    private Boolean isSuperUser = false;
}
