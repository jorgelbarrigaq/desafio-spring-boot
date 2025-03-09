package com.nuevo.tareas.repository;

import com.nuevo.tareas.model.EstadoTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoTareaRepository extends JpaRepository<EstadoTarea, Long> {
    Optional<EstadoTarea> findByNombre(String nombre);
}
