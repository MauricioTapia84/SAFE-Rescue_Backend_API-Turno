package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Turno.service.TipoEquipoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de prueba para el controlador TipoEquipoController.
 * Proporciona pruebas unitarias para verificar el correcto funcionamiento
 * de los endpoints relacionados con los tipos de equipo.
 */
@WebMvcTest(TipoEquipoController.class)
public class TipoEquipoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TipoEquipoService tipoEquipoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private TipoEquipo tipoEquipo;
    private Integer id;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * Inicializa Faker y crea un objeto TipoEquipo para las pruebas.
     */
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = 1;
        tipoEquipo = new TipoEquipo(1, faker.job().position());
    }

    /**
     * Prueba que verifica la obtención de todos los tipos de equipo existentes.
     * Asegura que se devuelve un estado 200 OK y la lista de tipos de equipo.
     */
    @Test
    public void listarTiposEquipoTest() throws Exception {
        // Arrange
        when(tipoEquipoService.findAll()).thenReturn(List.of(tipoEquipo));

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/tipos-equipo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(tipoEquipo.getId()))
                .andExpect(jsonPath("$[0].nombre").value(tipoEquipo.getNombre()));
    }

    /**
     * Prueba que verifica la búsqueda de un tipo de equipo existente por su ID.
     * Asegura que se devuelve un estado 200 OK y el tipo de equipo encontrado.
     */
    @Test
    public void buscarTipoEquipoTest() throws Exception {
        // Arrange
        when(tipoEquipoService.findById(id)).thenReturn(tipoEquipo);

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/tipos-equipo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tipoEquipo.getId()))
                .andExpect(jsonPath("$.nombre").value(tipoEquipo.getNombre()));
    }

    /**
     * Prueba que verifica la creación de un nuevo tipo de equipo.
     * Asegura que se devuelve un estado 201 CREATED al agregar un tipo de equipo exitosamente.
     */
    @Test
    public void agregarTipoEquipoTest() throws Exception {
        // Arrange
        when(tipoEquipoService.save(any(TipoEquipo.class))).thenReturn(tipoEquipo);

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/tipos-equipo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipoEquipo))) // Convertir TipoEquipo a JSON
                .andExpect(status().isCreated())
                .andExpect(content().string("Tipo Equipo creado con éxito."));
    }

    /**
     * Prueba que verifica la actualización de un tipo de equipo existente.
     * Asegura que se devuelve un estado 200 OK al actualizar correctamente.
     */
    @Test
    public void actualizarTipoEquipoTest() throws Exception {
        // Arrange
        when(tipoEquipoService.update(any(TipoEquipo.class), eq(id))).thenReturn(tipoEquipo);

        // Act & Assert
        mockMvc.perform(put("/api-turnos/v1/tipos-equipo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipoEquipo))) // Convertir TipoEquipo a JSON
                .andExpect(status().isOk())
                .andExpect(content().string("Actualizado con éxito"));
    }

    /**
     * Prueba que verifica la eliminación de un tipo de equipo existente.
     * Asegura que se devuelve un estado 200 OK al eliminar correctamente.
     */
    @Test
    public void eliminarTipoEquipoTest() throws Exception {
        // Arrange
        doNothing().when(tipoEquipoService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turnos/v1/tipos-equipo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Tipo Equipo eliminado con éxito."));
    }

    // ERRORES

    /**
     * Prueba que verifica el comportamiento cuando no hay tipos de equipo registrados.
     * Asegura que se devuelve un estado 204 NO CONTENT.
     */
    @Test
    public void listarTest_TiposEquipoNoExistentes() throws Exception {
        // Arrange
        when(tipoEquipoService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/tipos-equipo"))
                .andExpect(status().isNoContent());
    }

    /**
     * Prueba que verifica el comportamiento al buscar un tipo de equipo que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void buscarTipoEquipoTest_TipoNoExistente() throws Exception {
        // Arrange
        when(tipoEquipoService.findById(id)).thenThrow(new NoSuchElementException("Tipo Equipo no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/tipos-equipo/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tipo Equipo no encontrado"));
    }

    /**
     * Prueba que verifica el manejo de errores al intentar agregar un tipo de equipo.
     * Asegura que se devuelve un estado 400 BAD REQUEST al ocurrir un error.
     */
    @Test
    public void agregarTipoEquipoTest_Error() throws Exception {
        // Arrange
        when(tipoEquipoService.save(any(TipoEquipo.class))).thenThrow(new RuntimeException("Error al crear el tipo de equipo"));

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/tipos-equipo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipoEquipo))) // Convertir TipoEquipo a JSON
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear el tipo de equipo"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar actualizar un tipo de equipo que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void actualizarTipoEquipoTest_TipoNoExistente() throws Exception {
        // Arrange
        when(tipoEquipoService.update(any(TipoEquipo.class), eq(id))).thenThrow(new NoSuchElementException("Tipo Equipo no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api-turnos/v1/tipos-equipo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipoEquipo))) // Convertir TipoEquipo a JSON
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tipo Equipo no encontrado"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar eliminar un tipo de equipo que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void eliminarTipoEquipoTest_TipoNoExistente() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Tipo Equipo no encontrado")).when(tipoEquipoService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turnos/v1/tipos-equipo/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tipo Equipo no encontrado"));
    }
}