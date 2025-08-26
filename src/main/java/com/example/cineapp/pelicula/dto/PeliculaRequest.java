package com.example.cineapp.pelicula.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para crear o editar una película")
public class PeliculaRequest {

    @Schema(example = "Inception")
    private String titulo;

    @Schema(example = "Ciencia ficción")
    private String genero;

    @Schema(example = "Un ladrón que roba secretos mediante los sueños.")
    private String descripcion;

    @Schema(example = "https://example.com/poster.jpg")
    private String imagenUrl;
}