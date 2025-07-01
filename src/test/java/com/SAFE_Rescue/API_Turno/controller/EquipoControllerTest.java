package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.service.EquipoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de prueba para el controlador EquipoController.
 * Proporciona pruebas unitarias para verificar el correcto funcionamiento
 * de los endpoints relacionados con equipos de emergencia.
 */
@WebMvcTest(EquipoController.class)
public class EquipoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipoService equipoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private Random random;
    private Equipo equipo;
    private Integer id;
    private List<Vehiculo> vehiculosAsignados;
    private List<Bombero> personal;
    private List<Recurso> recursosAsignados;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * Inicializa Faker y crea un objeto Equipo para las pruebas.
     */
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        recursosAsignados = new ArrayList<>();
        personal = new ArrayList<>();
        vehiculosAsignados = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            vehiculosAsignados.add(new Vehiculo(1,faker.vehicle().make(),faker.vehicle().model(),faker.vehicle().licensePlate(),faker.name().firstName(),faker.random().nextBoolean()));
        }
        equipo.setVehiculos(vehiculosAsignados);

        for (int j = 0; j < 3; j++) {
            personal.add(new Bombero(1,faker.name().firstName(),faker.name().lastName(),faker.name().lastName(),faker.number().numberBetween(100000000, 999999999)));
        }
        equipo.setPersonal(personal);

        for (int j = 0; j < 3; j++) {
            recursosAsignados.add(new Recurso(1,faker.lorem().word(),faker.lorem().word(),faker.number().numberBetween(0,9999)));
        }
        equipo.setRecursos(recursosAsignados);

        Ubicacion ubicacion = new Ubicacion(1, faker.address().streetName(),
                faker.number().numberBetween(1, 9999),
                faker.address().city(),
                faker.address().state());
        Compania compania = new Compania(1, faker.company().name(), ubicacion);
        TipoEquipo tipoEquipo = new TipoEquipo(1, faker.animal().name());

        LocalDateTime fechaHoraInicio = LocalDateTime.now().plusDays(random.nextInt(10))
                .withHour(random.nextInt(24))
                .withMinute(random.nextInt(60));

        LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(8);

        Turno turno = new Turno(1, faker.name().title(), fechaHoraInicio, fechaHoraFin, Duration.between(fechaHoraInicio, fechaHoraFin).toHours());
        equipo = new Equipo(1, faker.name().firstName(), faker.number().numberBetween(0, 99), faker.random().nextBoolean(), faker.name().firstName(),vehiculosAsignados,personal, recursosAsignados, turno, compania, tipoEquipo);
        id = 1;
    }

    /**
     * Prueba que verifica la obtención de todos los equipos existentes.
     * Asegura que se devuelve un estado 200 OK y la lista de equipos.
     */
    @Test
    public void listarTest() throws Exception {
        // Arrange
        when(equipoService.findAll()).thenReturn(List.of(equipo));

        // Act & Assert
        mockMvc.perform(get("/api-turno/v1/equipos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(equipo.getId()))
                .andExpect(jsonPath("$[0].nombre").value(equipo.getNombre()))
                .andExpect(jsonPath("$[0].cantidadMiembros").value(equipo.getCantidadMiembros()))
                .andExpect(jsonPath("$[0].lider").value(equipo.getLider()));
    }

    /**
     * Prueba que verifica la búsqueda de un equipo existente por su ID.
     * Asegura que se devuelve un estado 200 OK y el equipo encontrado.
     */
    @Test
    public void buscarEquipoTest() throws Exception {
        // Arrange
        when(equipoService.findByID(id)).thenReturn(equipo);

        // Act & Assert
        mockMvc.perform(get("/api-turno/v1/equipos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipo.getId()))
                .andExpect(jsonPath("$.nombre").value(equipo.getNombre()))
                .andExpect(jsonPath("$.cantidadMiembros").value(equipo.getCantidadMiembros()))
                .andExpect(jsonPath("$.lider").value(equipo.getLider()));
    }

    /**
     * Prueba que verifica la creación de un nuevo equipo.
     * Asegura que se devuelve un estado 201 CREATED al agregar un equipo exitosamente.
     */
    @Test
    public void agregarEquipoTest() throws Exception {
        // Arrange
        when(equipoService.save(any(Equipo.class))).thenReturn(equipo);

        // Act & Assert
        mockMvc.perform(post("/api-turno/v1/equipos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipo))) // Convertir Equipo a JSON
                .andExpect(status().isCreated())
                .andExpect(content().string("Equipo creado con éxito."));
    }

    /**
     * Prueba que verifica la actualización de un equipo existente.
     * Asegura que se devuelve un estado 200 OK al actualizar correctamente.
     */
    @Test
    public void actualizarEquipoTest() throws Exception {
        // Arrange
        when(equipoService.update(any(Equipo.class), eq(id))).thenReturn(equipo);

        // Act & Assert
        mockMvc.perform(put("/api-turno/v1/equipos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipo))) // Convertir Equipo a JSON
                .andExpect(status().isOk())
                .andExpect(content().string("Actualizado con éxito"));
    }

    /**
     * Prueba que verifica la eliminación de un equipo existente.
     * Asegura que se devuelve un estado 200 OK al eliminar correctamente.
     */
    @Test
    public void eliminarEquipoTest() throws Exception {
        // Arrange
        doNothing().when(equipoService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turno/v1/equipos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Equipo eliminado con éxito."));
    }

    // ERRORES

    /**
     * Prueba que verifica el comportamiento cuando no hay equipos registrados.
     * Asegura que se devuelve un estado 204 NO CONTENT.
     */
    @Test
    public void listarTest_EquiposNoExistentes() throws Exception {
        // Arrange
        when(equipoService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api-turno/v1/equipos"))
                .andExpect(status().isNoContent());
    }

    /**
     * Prueba que verifica el comportamiento al buscar un equipo que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void buscarEquipoTest_EquipoNoExistente() throws Exception {
        // Arrange
        when(equipoService.findByID(id)).thenThrow(new NoSuchElementException("Equipo no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api-turno/v1/equipos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Equipo no encontrado"));
    }

    /**
     * Prueba que verifica el manejo de errores al intentar agregar un equipo.
     * Asegura que se devuelve un estado 400 BAD REQUEST al ocurrir un error.
     */
    @Test
    public void agregarEquipoTest_Error() throws Exception {
        // Arrange
        when(equipoService.save(any(Equipo.class))).thenThrow(new RuntimeException("Error al crear el equipo"));

        // Act & Assert
        mockMvc.perform(post("/api-turno/v1/equipos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipo))) // Convertir Equipo a JSON
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear el equipo"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar actualizar un equipo que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void actualizarEquipoTest_EquipoNoExistente() throws Exception {
        // Arrange
        when(equipoService.update(any(Equipo.class), eq(id))).thenThrow(new NoSuchElementException("Equipo no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api-turno/v1/equipos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipo))) // Convertir Equipo a JSON
                .andExpect(status().isNotFound())
                .andExpect(content().string("Equipo no encontrado"));
    }

    /**
     * Prueba que verifica el comportamiento al intentar eliminar un equipo que no existe.
     * Asegura que se devuelve un estado 404 NOT FOUND.
     */
    @Test
    public void eliminarEquipoTest_EquipoNoExistente() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Equipo no encontrado")).when(equipoService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api-turno/v1/equipos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Equipo no encontrado"));
    }
}