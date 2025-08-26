package com.example.cineapp.pelicula.controller;

import com.example.cineapp.pelicula.dto.PeliculaRequest;
import com.example.cineapp.pelicula.model.Pelicula;
import com.example.cineapp.pelicula.service.AmazonS3Service;
import com.example.cineapp.pelicula.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/peliculas")
@RequiredArgsConstructor
@Tag(name = "Películas", description = "Operaciones relacionadas con la cartelera y gestión de películas")
public class PeliculaController {

    private final PeliculaService peliculaService;
    private final AmazonS3Service amazonS3Service;

    @GetMapping
    @Operation(summary = "Listar cartelera activa", description = "Devuelve todas las películas activas (cartelera).")
    public ResponseEntity<List<Pelicula>> listarPeliculasActivas() {
        return ResponseEntity.ok(peliculaService.obtenerPeliculasActivas());
    }

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Crear nueva película con imagen", description = "Crea una nueva película y sube una imagen a Amazon S3")
    public ResponseEntity<Pelicula> crearPelicula(
            @RequestPart("titulo") String titulo,
            @RequestPart("genero") String genero,
            @RequestPart("descripcion") String descripcion,
            @RequestPart("imagen") MultipartFile imagen
    ) {
        String imagenUrl = amazonS3Service.uploadFile(imagen);

        PeliculaRequest request = new PeliculaRequest();
        request.setTitulo(titulo);
        request.setGenero(genero);
        request.setDescripcion(descripcion);
        request.setImagenUrl(imagenUrl);

        Pelicula pelicula = peliculaService.crearPelicula(request);
        return ResponseEntity.ok(pelicula);
    }


    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar película", description = "Actualiza los datos de una película por su ID. Solo para superusuarios.")
    public ResponseEntity<Pelicula> actualizarPelicula(@PathVariable Long id, @RequestBody PeliculaRequest request) {
        return ResponseEntity.ok(peliculaService.actualizarPelicula(id, request));
    }

    @PatchMapping("/{id}/inhabilitar")
    @Operation(summary = "Inhabilitar película", description = "Desactiva una película (no se mostrará en la cartelera). Solo superusuarios.")
    public ResponseEntity<?> inhabilitarPelicula(@PathVariable Long id) {
        peliculaService.inhabilitarPelicula(id);
        return ResponseEntity.ok("Película inhabilitada");
    }

    @GetMapping("/admin")
    @Operation(
            summary = "Listar todas las películas",
            description = "Devuelve todas las películas registradas, sin importar si están activas o inactivas. Solo para administradores."
    )
    public ResponseEntity<List<Pelicula>> listarPeliculasParaAdmin() {
        return ResponseEntity.ok(peliculaService.obtenerTodas());
    }

}
