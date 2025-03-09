package com.nuevo.tareas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nuevo.tareas.model.EstadoTarea;
import com.nuevo.tareas.model.Tarea;
import com.nuevo.tareas.model.Usuario;
import com.nuevo.tareas.repository.EstadoTareaRepository;
import com.nuevo.tareas.repository.TareaRepository;
import com.nuevo.tareas.repository.UsuarioRepository;
import com.nuevo.tareas.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class GestionTareasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${crendential-api-key.email}")
    private String adminEmail;

    @Value("${crendential-api-key.password}")
    private String adminPassword;

    private String token;
    private ObjectMapper objectMapper;

	@Autowired
	private EstadoTareaRepository estadoTareaRepository;


    @BeforeEach
    void setUp() {
        // Configurar Jackson para manejar LocalDateTime
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Verificar si el usuario ya existe, eliminar si es necesario
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(adminEmail);
        existingUser.ifPresent(usuarioRepository::delete);

        // Crear usuario de prueba con credenciales de application.yml
        Usuario usuario = new Usuario();
        usuario.setEmail(adminEmail);
        usuario.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
        usuarioRepository.save(usuario);

        // Generar token JWT para autenticación en pruebas
        token = jwtUtil.generateToken(usuario.getEmail());
    }

    @Test
    void contextLoads() {
    }

    // 🔹 PRUEBA: Login exitoso
    @Test
    void testLogin_Success() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + adminEmail + "\", \"password\":\"" + adminPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // 🔹 PRUEBA: Login fallido
    @Test
    void testLogin_Fail() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + adminEmail + "\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    // 🔹 PRUEBA: Obtener todas las tareas (debe estar autenticado)
    @Test
    void testGetTareas() throws Exception {
        mockMvc.perform(get("/tareas")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // 🔹 PRUEBA: Crear una tarea nueva
    @Test
    void testCrearTarea() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setTitulo("Nueva tarea");
        tarea.setDescripcion("Descripción de prueba");
        tarea.setFechaCreacion(LocalDateTime.now());
        tarea.setFechaVencimiento(LocalDateTime.now().plusDays(5));

        String tareaJson = objectMapper.writeValueAsString(tarea);

        mockMvc.perform(post("/tareas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Nueva tarea"));
    }

    // 🔹 PRUEBA: Actualizar una tarea existente
    @Test
	void testActualizarTarea() throws Exception {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(adminEmail);

		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();

			// Obtener estado "Pendiente"
			EstadoTarea estadoPendiente = estadoTareaRepository.findByNombre("Pendiente")
					.orElseGet(() -> estadoTareaRepository.save(new EstadoTarea(null, "Pendiente")));

			// Crear una tarea con estado asignado
			Tarea tarea = new Tarea();
			tarea.setTitulo("Tarea Original");
			tarea.setDescripcion("Descripción Original");
			tarea.setFechaCreacion(LocalDateTime.now());
			tarea.setFechaVencimiento(LocalDateTime.now().plusDays(5));
			tarea.setUsuario(usuario);
			tarea.setEstado(estadoPendiente); // 🔹 Asignar estado válido

			tarea = tareaRepository.save(tarea);

			// Datos actualizados
			tarea.setTitulo("Tarea Actualizada");
			tarea.setDescripcion("Nueva descripción");

			String tareaJson = objectMapper.writeValueAsString(tarea);

			mockMvc.perform(put("/tareas/" + tarea.getId())
					.header("Authorization", "Bearer " + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content(tareaJson))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.titulo").value("Tarea Actualizada"));
		}
	}


    // 🔹 PRUEBA: Eliminar una tarea
    @Test
	void testEliminarTarea() throws Exception {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(adminEmail);

		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();

			// Obtener estado "Pendiente"
			EstadoTarea estadoPendiente = estadoTareaRepository.findByNombre("Pendiente")
					.orElseGet(() -> estadoTareaRepository.save(new EstadoTarea(null, "Pendiente")));

			// Crear una tarea con estado asignado
			Tarea tarea = new Tarea();
			tarea.setTitulo("Tarea a Eliminar");
			tarea.setDescripcion("Se eliminará en la prueba");
			tarea.setFechaCreacion(LocalDateTime.now());
			tarea.setFechaVencimiento(LocalDateTime.now().plusDays(5));
			tarea.setUsuario(usuario);
			tarea.setEstado(estadoPendiente); // 🔹 Asignar estado válido

			tarea = tareaRepository.save(tarea);

			mockMvc.perform(delete("/tareas/" + tarea.getId())
					.header("Authorization", "Bearer " + token))
					.andExpect(status().isNoContent());
		}
	}

	}
