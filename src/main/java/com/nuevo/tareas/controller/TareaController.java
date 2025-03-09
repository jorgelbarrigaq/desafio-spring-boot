package com.nuevo.tareas.controller;

import com.nuevo.tareas.model.EstadoTarea;
import com.nuevo.tareas.model.Tarea;
import com.nuevo.tareas.model.Usuario;
import com.nuevo.tareas.repository.EstadoTareaRepository;
import com.nuevo.tareas.repository.TareaRepository;
import com.nuevo.tareas.repository.UsuarioRepository;
import com.nuevo.tareas.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tareas")
@Tag(name = "Tareas", description = "API para gestionar tareas del usuario autenticado")
public class TareaController {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoTareaRepository estadoTareaRepository;
    private final JwtUtil jwtUtil;

    public TareaController(TareaRepository tareaRepository, UsuarioRepository usuarioRepository,
                           EstadoTareaRepository estadoTareaRepository, JwtUtil jwtUtil) {
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoTareaRepository = estadoTareaRepository;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Obtener todas las tareas del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tareas obtenida con éxito"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<Tarea>> getTareas() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(403).build();
            }
            
            List<Tarea> tareas = tareaRepository.findByUsuarioId(usuarioOpt.get().getId());
            return ResponseEntity.ok(tareas);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Crear una nueva tarea")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tarea creada con éxito"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "Error interno al crear la tarea")
    })
    @PostMapping
    public ResponseEntity<Tarea> crearTarea(@RequestBody Tarea tarea) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(403).build();
            }

            Optional<EstadoTarea> estadoPendiente = estadoTareaRepository.findByNombre("Pendiente");
            if (estadoPendiente.isEmpty()) {
                return ResponseEntity.status(500).body(null);
            }

            tarea.setUsuario(usuarioOpt.get());
            tarea.setFechaCreacion(LocalDateTime.now());
            tarea.setEstado(estadoPendiente.get());

            Tarea nuevaTarea = tareaRepository.save(tarea);
            return ResponseEntity.ok(nuevaTarea);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Actualizar una tarea existente")
    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizarTarea(@PathVariable Long id, @RequestBody Tarea tareaActualizada) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(403).build();
            }

            Optional<Tarea> tareaOpt = tareaRepository.findById(id);

            if (tareaOpt.isPresent() && tareaOpt.get().getUsuario().getId().equals(usuarioOpt.get().getId())) {
                Tarea tarea = tareaOpt.get();
                tarea.setTitulo(tareaActualizada.getTitulo());
                tarea.setDescripcion(tareaActualizada.getDescripcion());
                tarea.setFechaVencimiento(tareaActualizada.getFechaVencimiento());

                tareaRepository.save(tarea);
                return ResponseEntity.ok(tarea);
            } else {
                return ResponseEntity.status(403).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Eliminar una tarea")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(403).build();
            }

            Optional<Tarea> tareaOpt = tareaRepository.findById(id);

            if (tareaOpt.isPresent() && tareaOpt.get().getUsuario().getId().equals(usuarioOpt.get().getId())) {
                tareaRepository.delete(tareaOpt.get());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}