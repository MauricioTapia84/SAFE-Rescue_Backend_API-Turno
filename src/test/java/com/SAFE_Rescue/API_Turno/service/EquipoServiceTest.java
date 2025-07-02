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
    private String patente;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * <p>
     * Inicializa Faker y crea una instancia de Equipo para las pruebas.
     * </p>
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
            return Optional.of(new Vehiculo(id, "Marca " + id, "Modelo " + id, "Patente " + id, "Conductor " + id, true));
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
            Vehiculo vehiculo = new Vehiculo(j + 1, faker.vehicle().make(), faker.vehicle().model(), String.valueOf(faker.number().numberBetween(0, 999999)), faker.name().firstName(), true);
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
        assertEquals(equipo.getCantidadMiembros(), equipos.get(0).getCantidadMiembros());
        assertEquals(equipo.isEstado(), equipos.get(0).isEstado());
        assertEquals(equipo.getLider(), equipos.get(0).getLider());
        assertEquals(equipo.getVehiculos(), equipos.get(0).getVehiculos());
        assertEquals(equipo.getPersonal(), equipos.get(0).getPersonal());
        assertEquals(equipo.getRecursos(), equipos.get(0).getRecursos());
        assertEquals(equipo.getTurno(), equipos.get(0).getTurno());
        assertEquals(equipo.getCompania(), equipos.get(0).getCompania());
        assertEquals(equipo.getTipoEquipo(), equipos.get(0).getTipoEquipo());
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
        assertEquals(equipo.getCantidadMiembros(), encontrado.getCantidadMiembros());
        assertEquals(equipo.isEstado(), encontrado.isEstado());
        assertEquals(equipo.getLider(), encontrado.getLider());
        assertEquals(equipo.getVehiculos(), encontrado.getVehiculos());
        assertEquals(equipo.getPersonal(), encontrado.getPersonal());
        assertEquals(equipo.getRecursos(), encontrado.getRecursos());
        assertEquals(equipo.getTurno(), encontrado.getTurno());
        assertEquals(equipo.getCompania(), encontrado.getCompania());
        assertEquals(equipo.getTipoEquipo(), encontrado.getTipoEquipo());
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
        assertEquals(equipo.getCantidadMiembros(), guardado.getCantidadMiembros());
        assertEquals(equipo.isEstado(), guardado.isEstado());
        assertEquals(equipo.getLider(), guardado.getLider());
        assertEquals(equipo.getVehiculos(), guardado.getVehiculos());
        assertEquals(equipo.getPersonal(), guardado.getPersonal());
        assertEquals(equipo.getRecursos(), guardado.getRecursos());
        assertEquals(equipo.getTurno(), guardado.getTurno());
        assertEquals(equipo.getCompania(), guardado.getCompania());
        assertEquals(equipo.getTipoEquipo(), guardado.getTipoEquipo());

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
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipo);
        when(tipoEquipoRepository.findById(1)).thenReturn(Optional.of(new TipoEquipo(1, "Tipo de Ejemplo")));

        Equipo nuevoEquipo = new Equipo(
                1, "Equipo Actualizado", 10, false, "Líder Actualizado",
                vehiculosAsignados, personal, recursosAsignados,
                equipo.getTurno(),
                equipo.getCompania(),
                equipo.getTipoEquipo()
        );

        // Act
        Equipo actualizado = equipoService.update(nuevoEquipo, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals(equipo.getNombre(), actualizado.getNombre(), "El nombre del equipo no coincide");
        assertEquals(equipo.getCantidadMiembros(), actualizado.getCantidadMiembros(), "La cantidad de miembros no coincide");
        assertEquals(equipo.getLider(), actualizado.getLider(), "El nombre del líder no coincide");
        assertEquals(equipo.isEstado(), actualizado.isEstado(), "El estado del equipo no coincide");

        // Verificar listas de vehículos
        assertEquals(equipo.getVehiculos().size(), actualizado.getVehiculos().size(), "La cantidad de vehículos no coincide");
        for (int i = 0; i < equipo.getVehiculos().size(); i++) {
            assertEquals(equipo.getVehiculos().get(i), actualizado.getVehiculos().get(i), "El vehículo en la posición " + i + " no coincide");
        }

        // Verificar listas de personal
        assertEquals(equipo.getPersonal().size(), actualizado.getPersonal().size(), "La cantidad de personal no coincide");
        for (int i = 0; i < equipo.getPersonal().size(); i++) {
            assertEquals(equipo.getPersonal().get(i), actualizado.getPersonal().get(i), "El personal en la posición " + i + " no coincide");
        }

        // Verificar listas de recursos
        assertEquals(equipo.getRecursos().size(), actualizado.getRecursos().size(), "La cantidad de recursos no coincide");
        for (int i = 0; i < equipo.getRecursos().size(); i++) {
            assertEquals(equipo.getRecursos().get(i), actualizado.getRecursos().get(i), "El recurso en la posición " + i + " no coincide");
        }

        assertEquals(equipo.getTurno(), actualizado.getTurno(), "El turno no coincide");
        assertEquals(equipo.getCompania(), actualizado.getCompania(), "La compañía no coincide");
        assertEquals(equipo.getTipoEquipo(), actualizado.getTipoEquipo(), "El tipo de equipo no coincide");

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
        equipo.setNombre(null);

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