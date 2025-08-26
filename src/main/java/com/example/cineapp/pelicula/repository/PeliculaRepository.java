package com.example.cineapp.pelicula.repository;

import com.example.cineapp.pelicula.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByActiveTrue(); // para mostrar solo cartelera activa
}
