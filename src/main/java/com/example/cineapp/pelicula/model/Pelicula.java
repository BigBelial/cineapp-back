package com.example.cineapp.pelicula.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peliculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una película")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la película", example = "1")
    private Long id;

    @Schema(description = "Título de la película", example = "Inception")
    private String titulo;

    @Schema(description = "Género de la película", example = "Ciencia ficción")
    private String genero;

    @Schema(description = "Descripción de la película", example = "Un ladrón experto en robar secretos del subconsciente.")
    private String descripcion;

    @Schema(description = "URL de la imagen almacenada en Amazon S3", example = "https://cineapp.s3.amazonaws.com/inception.jpg")
    private String imagenUrl;

    @Schema(description = "Estado activo de la película", example = "true")
    private Boolean active = true;
}