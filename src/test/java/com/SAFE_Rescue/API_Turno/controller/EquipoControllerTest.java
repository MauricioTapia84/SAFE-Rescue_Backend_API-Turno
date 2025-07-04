package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.repository.BomberoRepository;
import com.SAFE_Rescue.API_Turno.repository.RecursoRepository;
import com.SAFE_Rescue.API_Turno.repository.VehiculoRepository;
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

    @MockitoBean
    private RecursoRepository recursoRepository;

    @MockitoBean
    private VehiculoRepository vehiculoRepository;

    @MockitoBean
    private BomberoRepository bomberoRepository;

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
        id = 1;
        faker = new Faker();
        random = new Random();
        recursosAsignados = new ArrayList<>();
        personal = new ArrayList<>();
        vehiculosAsignados = new ArrayList<>();

        // Mockear los vehículos
        when(vehiculoRepository.findById(anyInt())).thenAnswer(invocation -> {
            Integer id = invocation.getArgument(0);
            return Optional.of(new Vehiculo(id, "Marca " + id, "Modelo " + id, "Patente " + id, "Conductor " + id, faker.lorem().word()));
        });

        // Mockear el personal (Bomberos)
        when(bomberoRepository.findById(anyInt())).thenAnswer(invocation -> {
            Integer id = invocation.getArgument(0);
            return Optional.of(new Bombero(id, faker.name().firstName(), faker.name().lastName(), faker.name().lastName(), faker.number().numberBetween(100000000, 999999999)));
        });

        // Mockear los recursos
        when(recursoRepository.findById(anyInt())).thenAnswer(invocation -> {
            Integer id = invocation.getArgument(0);
            return Optional.of(new Recurso(id, faker.lorem().word(), faker.lorem().word(), faker.number().numberBetween(0, 9999)));
        });

        // Crear vehículos
        for (int j = 0; j < 2; j++) {
            Vehiculo vehiculo = new Vehiculo(j + 1, faker.vehicle().make(), faker.vehicle().model(), String.valueOf(faker.number().numberBetween(0, 999999)), faker.name().firstName(), faker.lorem().word());
            vehiculosAsignados.add(vehiculo);
        }

        // Crear personal (Bomberos)
        for (int j = 0; j < 2; j++) {
            Bombero bombero = new Bombero(j + 1, faker.name().firstName(), faker.name().lastName(), faker.name().lastName(), faker.number().numberBetween(100000000, 999999999));
            personal.add(bombero);
        }

        // Crear recursos
        for (int j = 0; j < 2; j++) {
            Recurso recurso = new Recurso(j + 1, faker.lorem().word(), faker.lorem().word(), faker.number().numberBetween(0, 9999));
            recursosAsignados.add(recurso);
        }

        Ubicacion ubicacion = new Ubicacion(1, faker.address().streetName(), faker.number().numberBetween(1, 9999), faker.address().city(), faker.address().state());
        Compania compania = new Compania(1, faker.company().name(), ubicacion);
        TipoEquipo tipoEquipo = new TipoEquipo(1, faker.animal().name());

        LocalDateTime fechaHoraInicio = LocalDateTime.now().plusDays(random.nextInt(10)).withHour(random.nextInt(24)).withMinute(random.nextInt(60));
        LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(8);
        Turno turno = new Turno(1, faker.name().title(), fechaHoraInicio, fechaHoraFin, (int)Duration.between(fechaHoraInicio, fechaHoraFin).toHours());

        equipo = new Equipo(1, faker.name().firstName(), faker.number().numberBetween(1, 99), faker.random().nextBoolean(), faker.name().firstName(), vehiculosAsignados, personal, recursosAsignados, turno, compania, tipoEquipo);
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
        mockMvc.perform(get("/api-turnos/v1/equipos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(equipo.getId()))
                .andExpect(jsonPath("$[0].nombre").value(equipo.getNombre()))
                .andExpect(jsonPath("$[0].cantidadMiembros").value(equipo.getCantidadMiembros()))
                .andExpect(jsonPath("$[0].lider").value(equipo.getLider()))
                .andExpect(jsonPath("$[0].estado").value(equipo.isEstado()))
                .andExpect(jsonPath("$[0].vehiculos[0].id").value(equipo.getVehiculos().get(0).getId()))
                .andExpect(jsonPath("$[0].vehiculos[0].marca").value(equipo.getVehiculos().get(0).getMarca()))
                .andExpect(jsonPath("$[0].personal[0].id").value(equipo.getPersonal().get(0).getId()))
                .andExpect(jsonPath("$[0].recursos[0].id").value(equipo.getRecursos().get(0).getId()))
                .andExpect(jsonPath("$[0].turno.nombre").value(equipo.getTurno().getNombre()))
                .andExpect(jsonPath("$[0].compania.nombre").value(equipo.getCompania().getNombre()))
                .andExpect(jsonPath("$[0].tipoEquipo.nombre").value(equipo.getTipoEquipo().getNombre()));
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
        mockMvc.perform(get("/api-turnos/v1/equipos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipo.getId()))
                .andExpect(jsonPath("$.nombre").value(equipo.getNombre()))
                .andExpect(jsonPath("$.cantidadMiembros").value(equipo.getCantidadMiembros()))
                .andExpect(jsonPath("$.lider").value(equipo.getLider()))
                .andExpect(jsonPath("$.estado").value(equipo.isEstado()))
                .andExpect(jsonPath("$.vehiculos[0].id").value(equipo.getVehiculos().get(0).getId()))
                .andExpect(jsonPath("$.personal[0].id").value(equipo.getPersonal().get(0).getId()))
                .andExpect(jsonPath("$.recursos[0].id").value(equipo.getRecursos().get(0).getId()))
                .andExpect(jsonPath("$.turno.nombre").value(equipo.getTurno().getNombre()))
                .andExpect(jsonPath("$.compania.nombre").value(equipo.getCompania().getNombre()))
                .andExpect(jsonPath("$.tipoEquipo.nombre").value(equipo.getTipoEquipo().getNombre()));
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
        mockMvc.perform(post("/api-turnos/v1/equipos")
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
        mockMvc.perform(put("/api-turnos/v1/equipos/{id}", id)
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
        mockMvc.perform(delete("/api-turnos/v1/equipos/{id}", id))
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
        mockMvc.perform(get("/api-turnos/v1/equipos"))
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
        mockMvc.perform(get("/api-turnos/v1/equipos/{id}", id))
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
        mockMvc.perform(post("/api-turnos/v1/equipos")
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
        mockMvc.perform(put("/api-turnos/v1/equipos/{id}", id)
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
        mockMvc.perform(delete("/api-turnos/v1/equipos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Equipo no encontrado"));
    }
}