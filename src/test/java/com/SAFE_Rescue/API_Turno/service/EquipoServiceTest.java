package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.repository.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio EquipoService.
 * <p>
 * Esta clase verifica la funcionalidad de los métodos dentro de EquipoService,
 * incluyendo operaciones CRUD, validaciones y asignaciones.
 * </p>
 */
@SpringBootTest
public class EquipoServiceTest {

    @Autowired
    private EquipoService equipoService;

    @MockitoBean
    private EquipoRepository equipoRepository;

    @MockitoBean
    private BomberoRepository bomberoRepository;

    @MockitoBean
    private CompaniaRepository companiaRepository;

    @MockitoBean
    private TipoEquipoRepository tipoEquipoRepository;

    @MockitoBean
    private TurnoRepository turnoRepository;

    @MockitoBean
    private RecursoRepository recursoRepository;

    @MockitoBean
    private VehiculoRepository vehiculoRepository;

    private Faker faker;
    private Random random;
    private Equipo equipo;
    private Integer id;
    private List<Vehiculo> vehiculosAsignados;
    private List<Bombero> personal;
    private List<Recurso> recursosAsignados;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * <p>
     * Inicializa Faker y crea una instancia de Equipo para las pruebas.
     * </p>
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
     * Prueba que verifica la obtención de todos los equipos.
     * <p>
     * Asegura que el servicio devuelve la lista correcta de equipos.
     * </p>
     */
    @Test
    public void findAllTest() {
        // Arrange
        when(equipoRepository.findAll()).thenReturn(List.of(equipo));

        // Act
        List<Equipo> equipos = equipoService.findAll();

        // Assert
        assertNotNull(equipos);
        assertEquals(1, equipos.size());
        assertEquals(equipo.getNombre(), equipos.get(0).getNombre());
    }

    /**
     * Prueba que verifica la búsqueda de un equipo por su ID.
     * <p>
     * Asegura que se encuentra el equipo correcto.
     * </p>
     */
    @Test
    public void findByIdTest() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.of(equipo));

        // Act
        Equipo encontrado = equipoService.findByID(id);

        // Assert
        assertNotNull(encontrado);
        assertEquals(id, encontrado.getId());
        assertEquals(equipo.getNombre(), encontrado.getNombre());
    }

    /**
     * Prueba que verifica la creación y guardado de un nuevo equipo.
     * <p>
     * Asegura que el equipo se guarda correctamente en el repositorio.
     * </p>
     */
    @Test
    public void saveTest() {
        // Arrange
        when(turnoRepository.save(equipo.getTurno())).thenReturn(equipo.getTurno());
        when(companiaRepository.save(equipo.getCompania())).thenReturn(equipo.getCompania());
        when(tipoEquipoRepository.save(equipo.getTipoEquipo())).thenReturn(equipo.getTipoEquipo());
        when(equipoRepository.save(equipo)).thenReturn(equipo);

        // Act
        Equipo guardado = equipoService.save(equipo);

        // Assert
        assertNotNull(guardado);
        assertEquals(equipo.getNombre(), guardado.getNombre());
        verify(equipoRepository, times(1)).save(equipo);
    }

    /**
     * Prueba que verifica la actualización de un equipo existente.
     * <p>
     * Asegura que el equipo se actualiza correctamente.
     * </p>
     */
    @Test
    public void updateTest() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.of(equipo));
        when(equipoRepository.save(equipo)).thenReturn(equipo);

        Equipo nuevoEquipo = equipo;

        // Act
        Equipo actualizado = equipoService.update(nuevoEquipo, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals(nuevoEquipo.getCantidadMiembros(), actualizado.getCantidadMiembros());
        verify(equipoRepository, times(1)).save(equipo);
    }

    /**
     * Prueba que verifica la eliminación de un equipo.
     * <p>
     * Asegura que el equipo se elimina correctamente del repositorio.
     * </p>
     */
    @Test
    public void deleteTest() {
        // Arrange
        when(equipoRepository.existsById(id)).thenReturn(true);
        doNothing().when(equipoRepository).deleteById(id);

        // Act
        equipoService.delete(id);

        // Assert
        verify(equipoRepository, times(1)).deleteById(id);
    }

    /**
     * Prueba que verifica la búsqueda por ID cuando el equipo no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void findByIdTest_EquipoNoExiste() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> equipoService.findByID(id));
    }

    /**
     * Prueba que verifica el intento de actualización de un equipo que no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void testUpdate_EquipoNoExistente() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> equipoService.update(new Equipo(), id));
    }

    /**
     * Prueba que verifica la eliminación de un equipo que no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void testDelete_EquipoNoExistente() {
        // Arrange
        when(equipoRepository.existsById(id)).thenReturn(false);

        // Assert
        assertThrows(NoSuchElementException.class, () -> equipoService.delete(id));
    }

    /**
     * Prueba que verifica la validación de un equipo con nombre vacío.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarEquipo_NombreVacio() {
        // Arrange
        equipo.setNombre("");

        // Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.validarEquipo(equipo));
    }

    /**
     * Prueba que verifica la validación de un equipo con nombre demasiado largo.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarEquipo_NombreDemasiadoLargo() {
        // Arrange
        equipo.setNombre("A".repeat(51)); // 51 caracteres

        // Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.validarEquipo(equipo));
    }

    /**
     * Prueba que verifica la validación de un equipo con número de miembros negativo.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarEquipo_CantidadMiembrosNegativa() {
        // Arrange
        equipo.setCantidadMiembros(-1);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.validarEquipo(equipo));
    }

    /**
     * Prueba que verifica la validación de un equipo sin líder.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarEquipo_SinLider() {
        // Arrange
        equipo.setLider(null);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.validarEquipo(equipo));
    }
}