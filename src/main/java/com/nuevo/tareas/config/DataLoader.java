package com.nuevo.tareas.config;

import com.nuevo.tareas.model.EstadoTarea;
import com.nuevo.tareas.model.Usuario;
import com.nuevo.tareas.repository.EstadoTareaRepository;
import com.nuevo.tareas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Value("${crendential-api-key.email}") 
    private String adminEmail;

    @Value("${crendential-api-key.password}") 
    private String adminPassword;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, EstadoTareaRepository estadoRepo) {
        return args -> {
            // Precargar estados de tarea
            if (estadoRepo.count() == 0) {
                estadoRepo.saveAll(List.of(
                        new EstadoTarea(null, "Pendiente"),
                        new EstadoTarea(null, "En Progreso"),
                        new EstadoTarea(null, "Completada")
                ));
            }

            // Precargar usuario de prueba desde application.yml
            if (usuarioRepo.count() == 0) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                usuarioRepo.save(new Usuario(null, adminEmail, passwordEncoder.encode(adminPassword), null));
                System.out.println("Usuario admin cargado desde configuración: " + adminEmail);
            }
        };
    }
}
