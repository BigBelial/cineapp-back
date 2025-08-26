package com.example.cineapp.pelicula.service;

import com.example.cineapp.pelicula.dto.PeliculaRequest;
import com.example.cineapp.pelicula.model.Pelicula;
import com.example.cineapp.pelicula.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public List<Pelicula> obtenerPeliculasActivas() {
        return peliculaRepository.findByActiveTrue();
    }

    public Pelicula crearPelicula(PeliculaRequest request) {
        Pelicula pelicula = Pelicula.builder()
                .titulo(request.getTitulo())
                .genero(request.getGenero())
                .descripcion(request.getDescripcion())
                .imagenUrl(request.getImagenUrl()) // Por ahora, una URL simulada
                .active(true)
                .build();

        return peliculaRepository.save(pelicula);
    }

    public Optional<Pelicula> obtenerPeliculaPorId(Long id) {
        return peliculaRepository.findById(id);
    }

    public Pelicula actualizarPelicula(Long id, PeliculaRequest request) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        pelicula.setTitulo(request.getTitulo());
        pelicula.setGenero(request.getGenero());
        pelicula.setDescripcion(request.getDescripcion());
        pelicula.setImagenUrl(request.getImagenUrl());

        return peliculaRepository.save(pelicula);
    }

    public void inhabilitarPelicula(Long id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        pelicula.setActive(false);
        peliculaRepository.save(pelicula);
    }

    public List<Pelicula> obtenerTodas() {
        return peliculaRepository.findAll();
    }
}