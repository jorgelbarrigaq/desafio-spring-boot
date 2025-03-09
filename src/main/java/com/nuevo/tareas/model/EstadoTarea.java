package com.nuevo.tareas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_tarea")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}

