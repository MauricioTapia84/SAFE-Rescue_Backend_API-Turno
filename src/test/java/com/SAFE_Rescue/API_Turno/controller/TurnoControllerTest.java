package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.Turno;
import com.SAFE_Rescue.API_Turno.service.TurnoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de prueba para el controlador TurnoController.
 * Proporciona pruebas unitarias para verificar el correcto funcionamiento
 * de los endpoints relacionados con los turnos de emergencia.
 */
@WebMvcTest(TurnoController.class)
public class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private Turno turno;
    private Integer id;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * Inicializa Faker y crea un objeto Turno para las pruebas.
     */
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        turno = new Turno();
        id = 1;
        turno.setId(id);
        turno.setNombre(faker.name().title());
        turno.setFechaHoraInicio(LocalDateTime.now());
        turno.setFechaHoraFin(turno.getFechaHoraInicio().plusHours(8));
    }

    /**
     * Prueba que verifica la obtención de todos los turnos existentes.
     * Asegura que se devuelve un estado 200 OK y la lista de turnos.
     */
    @Test
    public void listarTest() throws Exception {
        // Arrange
        when(turnoService.findAll()).thenReturn(List.of(turno));

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(turno.getId()))
                .andExpect(jsonPath("$[0].nombre").value(turno.getNombre()));
    }

    /**
     * Prueba que verifica la búsqueda de un turno existente por su ID.
     * Asegura que se devuelve un estado 200 OK y el turno encontrado.
     */
    @Test
    public void buscarTurnoTest() throws Exception {
        // Arrange
        when(turnoService.findByID(id)).thenReturn(turno);

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/turnos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(turno.getId()))
                .andExpect(jsonPath("$.nombre").value(turno.getNombre()));
    }

    /**
     * Prueba que verifica la creación de un nuevo turno.
     * Asegura que se devuelve un estado 201 CREATED al agregar un turno exitosamente.
     */
    @Test
    public void agregarTurnoTest() throws Exception {
        // Arrange
        when(turnoService.save(any(Turno.class))).thenReturn(turno);

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turno))) // Convertir Turno a JSON
                .andExpect(status().isCreated())
                .andExpect(content().string("Turno creado con éxito."));
    }

    /**
     * Prueba que verifica la actualización de un turno existente.
     * Asegura que se devuelve un estado 200 OK al actualizar correctamente.
     */
    @Test
    public void actualizarTurnoTest() throws Exception {
        // Arrange
        when(turnoService.update(any(Turno.class), eq(id))).thenReturn(turno);

        // Act & Assert
        mockMvc.perform(put("/api-turnos/v1/turnos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turno))) // Convertir Turno a JSON
                .andExpect(status().isOk())
                .andExpect(content().string("Actualizado con éxito"));
    }

    /**
     * Prueba que verifica la eliminación de un turno existente.
     * Asegura que se devuelve un estado 200 OK al eliminar correctamente.
     */
    @Test
    public void eliminarTurnoTest() throws Exception {
        // Arrange
        doNothing().when(turnoService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turnos/v1/turnos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Turno eliminado con éxito."));
    }

    // ERRORES

    /**
     * Prueba que verifica el comportamiento cuando no hay turnos registrados.
     * Asegura que se devuelve un estado 204 NO CONTENT.
     */
    @Test
    public void listarTest_TurnosNoExistentes() throws Exception {
        // Arrange
        when(turnoService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/turnos"))
                .andExpect(status().isNoContent());
    }

    /**
     * Prueba que verifica el comportamiento al buscar un turno que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void buscarTurnoTest_TurnoNoExistente() throws Exception {
        // Arrange
        when(turnoService.findByID(id)).thenThrow(new NoSuchElementException("Turno no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/turnos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Turno no encontrado"));
    }

    /**
     * Prueba que verifica el manejo de errores al intentar agregar un turno.
     * Asegura que se devuelve un estado 400 BAD REQUEST al ocurrir un error.
     */
    @Test
    public void agregarTurnoTest_Error() throws Exception {
        // Arrange
        when(turnoService.save(any(Turno.class))).thenThrow(new RuntimeException("Error al crear el turno"));

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turno))) // Convertir Turno a JSON
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear el turno"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar actualizar un turno que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void actualizarTurnoTest_TurnoNoExistente() throws Exception {
        // Arrange
        when(turnoService.update(any(Turno.class), eq(id))).thenThrow(new NoSuchElementException("Turno no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api-turnos/v1/turnos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turno))) // Convertir Turno a JSON
                .andExpect(status().isNotFound())
                .andExpect(content().string("Turno no encontrado"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar eliminar un turno que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void eliminarTurnoTest_TurnoNoExistente() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Turno no encontrado")).when(turnoService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turnos/v1/turnos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Turno no encontrado"));
    }
}