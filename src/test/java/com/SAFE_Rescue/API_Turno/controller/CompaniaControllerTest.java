package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.service.CompaniaService;
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
 * Clase de prueba para el controlador CompaniaController.
 * Proporciona pruebas unitarias para verificar el correcto funcionamiento
 * de los endpoints relacionados con las compañías de bomberos.
 */
@WebMvcTest(CompaniaController.class)
public class CompaniaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompaniaService companiaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private Compania compania;
    private Integer id;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * Inicializa Faker y crea un objeto Compania para las pruebas.
     */
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        compania = new Compania();
        id = 1;
        compania.setId(id);
        compania.setNombre(faker.company().name());
    }

    /**
     * Prueba que verifica la obtención de todas las compañías existentes.
     * Asegura que se devuelve un estado 200 OK y la lista de compañías.
     */
    @Test
    public void listarCompaniasTest() throws Exception {
        // Arrange
        when(companiaService.findAll()).thenReturn(List.of(compania));

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/companias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(compania.getId()))
                .andExpect(jsonPath("$[0].nombre").value(compania.getNombre()));
    }

    /**
     * Prueba que verifica la búsqueda de una compañía existente por su ID.
     * Asegura que se devuelve un estado 200 OK y la compañía encontrada.
     */
    @Test
    public void buscarCompaniaTest() throws Exception {
        // Arrange
        when(companiaService.findByID(id)).thenReturn(compania);

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/companias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(compania.getId()))
                .andExpect(jsonPath("$.nombre").value(compania.getNombre()));
    }

    /**
     * Prueba que verifica la creación de una nueva compañía.
     * Asegura que se devuelve un estado 201 CREATED al agregar una compañía exitosamente.
     */
    @Test
    public void agregarCompaniaTest() throws Exception {
        // Arrange
        when(companiaService.save(any(Compania.class))).thenReturn(compania);

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/companias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compania))) // Convertir Compania a JSON
                .andExpect(status().isCreated())
                .andExpect(content().string("Compania creada con éxito."));
    }

    /**
     * Prueba que verifica la actualización de una compañía existente.
     * Asegura que se devuelve un estado 200 OK al actualizar correctamente.
     */
    @Test
    public void actualizarCompaniaTest() throws Exception {
        // Arrange
        when(companiaService.update(any(Compania.class), eq(id))).thenReturn(compania);

        // Act & Assert
        mockMvc.perform(put("/api-turnos/v1/companias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compania))) // Convertir Compania a JSON
                .andExpect(status().isOk())
                .andExpect(content().string("Actualizado con éxito"));
    }

    /**
     * Prueba que verifica la eliminación de una compañía existente.
     * Asegura que se devuelve un estado 200 OK al eliminar correctamente.
     */
    @Test
    public void eliminarCompaniaTest() throws Exception {
        // Arrange
        doNothing().when(companiaService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turnos/v1/companias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Compania eliminada con éxito."));
    }

    // ERRORES

    /**
     * Prueba que verifica el comportamiento cuando no hay compañías registradas.
     * Asegura que se devuelve un estado 204 NO CONTENT.
     */
    @Test
    public void listarTest_CompaniasNoExistentes() throws Exception {
        // Arrange
        when(companiaService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/companias"))
                .andExpect(status().isNoContent());
    }

    /**
     * Prueba que verifica el comportamiento al buscar una compañía que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void buscarCompaniaTest_CompaniaNoExistente() throws Exception {
        // Arrange
        when(companiaService.findByID(id)).thenThrow(new NoSuchElementException("Compania no encontrada"));

        // Act & Assert
        mockMvc.perform(get("/api-turnos/v1/companias/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compania no encontrada"));
    }

    /**
     * Prueba que verifica el manejo de errores al intentar agregar una compañía.
     * Asegura que se devuelve un estado 400 BAD REQUEST al ocurrir un error.
     */
    @Test
    public void agregarCompaniaTest_Error() throws Exception {
        // Arrange
        when(companiaService.save(any(Compania.class))).thenThrow(new RuntimeException("Error al crear la compañía"));

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/companias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compania))) // Convertir Compania a JSON
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear la compañía"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar actualizar una compañía que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void actualizarCompaniaTest_CompaniaNoExistente() throws Exception {
        // Arrange
        when(companiaService.update(any(Compania.class), eq(id))).thenThrow(new NoSuchElementException("Compania no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/api-turnos/v1/companias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compania))) // Convertir Compania a JSON
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compania no encontrada"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar eliminar una compañía que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void eliminarCompaniaTest_CompaniaNoExistente() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Compania no encontrada")).when(companiaService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turnos/v1/companias/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compania no encontrada"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar asignar una ubicación a una compañía que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void asignarUbicacionTest_CompaniaNoEncontrada() throws Exception {
        // Arrange
        Integer companiaId = 1;
        Integer ubicacionId = 1;
        doThrow(new RuntimeException("Compania no encontrada")).when(companiaService).asignarUbicacion(companiaId, ubicacionId);

        // Act & Assert
        mockMvc.perform(post("/api-turnos/v1/companias/{companiaId}/asignar-ubicacion/{ubicacionId}", companiaId, ubicacionId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Compania no encontrada"));
    }
}