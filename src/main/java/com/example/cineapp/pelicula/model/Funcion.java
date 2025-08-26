package com.example.cineapp.pelicula.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Funcion {
    public static final int CAPACIDAD_MAXIMA = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Pelicula pelicula;

    private String sala;

    private LocalDateTime fechaHora;

    private Integer cuposDisponibles = CAPACIDAD_MAXIMA;

    private Boolean active = true;
}
