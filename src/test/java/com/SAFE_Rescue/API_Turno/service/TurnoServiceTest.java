package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Turno;
import com.SAFE_Rescue.API_Turno.repository.TurnoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio TurnoService.
 * <p>
 * Esta clase verifica la funcionalidad de los métodos dentro de TurnoService,
 * incluyendo operaciones CRUD y validaciones.
 * </p>
 */
@SpringBootTest
public class TurnoServiceTest {

    @Autowired
    private TurnoService turnoService;

    @MockitoBean
    private TurnoRepository turnoRepository;

    private Faker faker;
    private Turno turno;
    private Integer id;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * <p>
     * Inicializa Faker y crea una instancia de Turno para las pruebas.
     * </p>
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
     * Prueba que verifica la obtención de todos los turnos.
     * <p>
     * Asegura que el servicio devuelve la lista correcta de turnos.
     * </p>
     */
    @Test
    public void findAllTest() {
        // Arrange
        when(turnoRepository.findAll()).thenReturn(List.of(turno));

        // Act
        List<Turno> turnos = turnoService.findAll();

        // Assert
        assertNotNull(turnos);
        assertEquals(1, turnos.size());
        assertEquals(turno.getNombre(), turnos.get(0).getNombre());
    }

    /**
     * Prueba que verifica la búsqueda de un turno por su ID.
     * <p>
     * Asegura que se encuentra el turno correcto.
     * </p>
     */
    @Test
    public void findByIdTest() {
        // Arrange
        when(turnoRepository.findById(id)).thenReturn(Optional.of(turno));

        // Act
        Turno encontrado = turnoService.findByID(id);

        // Assert
        assertNotNull(encontrado);
        assertEquals(id, encontrado.getId());
        assertEquals(turno.getNombre(), encontrado.getNombre());
    }

    /**
     * Prueba que verifica la creación y guardado de un nuevo turno.
     * <p>
     * Asegura que el turno se guarda correctamente en el repositorio.
     * </p>
     */
    @Test
    public void saveTest() {
        // Arrange
        when(turnoRepository.save(turno)).thenReturn(turno);

        // Act
        Turno guardado = turnoService.save(turno);

        // Assert
        assertNotNull(guardado);
        assertEquals(turno.getNombre(), guardado.getNombre());
        verify(turnoRepository, times(1)).save(turno);
    }

    /**
     * Prueba que verifica la actualización de un turno existente.
     * <p>
     * Asegura que el turno se actualiza correctamente.
     * </p>
     */
    @Test
    public void updateTest() {
        // Arrange
        when(turnoRepository.findById(id)).thenReturn(Optional.of(turno));
        when(turnoRepository.save(turno)).thenReturn(turno);

        Turno nuevoTurno = new Turno();
        nuevoTurno.setNombre(faker.name().title());
        nuevoTurno.setFechaHoraInicio(turno.getFechaHoraInicio());
        nuevoTurno.setFechaHoraFin(turno.getFechaHoraFin());

        // Act
        Turno actualizado = turnoService.update(nuevoTurno, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals(nuevoTurno.getNombre(), actualizado.getNombre());
        verify(turnoRepository, times(1)).save(turno);
    }

    /**
     * Prueba que verifica la eliminación de un turno.
     * <p>
     * Asegura que el turno se elimina correctamente del repositorio.
     * </p>
     */
    @Test
    public void deleteTest() {
        // Arrange
        when(turnoRepository.existsById(id)).thenReturn(true);
        doNothing().when(turnoRepository).deleteById(id);

        // Act
        turnoService.delete(id);

        // Assert
        verify(turnoRepository, times(1)).deleteById(id);
    }

    /**
     * Prueba que verifica la búsqueda por ID cuando el turno no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void findByIdTest_TurnoNoExiste() {
        // Arrange
        when(turnoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> turnoService.findByID(id));
    }

    /**
     * Prueba que verifica el intento de actualización de un turno que no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void testUpdate_TurnoNoExistente() {
        // Arrange
        when(turnoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> turnoService.update(new Turno(), id));
    }

    /**
     * Prueba que verifica la eliminación de un turno que no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void testDelete_TurnoNoExistente() {
        // Arrange
        when(turnoRepository.existsById(id)).thenReturn(false);

        // Assert
        assertThrows(NoSuchElementException.class, () -> turnoService.delete(id));
    }

    /**
     * Prueba que verifica la validación de un turno con nombre vacío.
     * <p>
     * Asegura que se lanza RuntimeException.
     * </p>
     */
    @Test
    public void validarTurno_NombreVacio() {
        // Arrange
        turno.setNombre("");

        // Assert
        assertThrows(RuntimeException.class, () -> turnoService.validarTurno(turno));
    }

    /**
     * Prueba que verifica la validación de un turno con nombre demasiado largo.
     * <p>
     * Asegura que se lanza RuntimeException.
     * </p>
     */
    @Test
    public void validarTurno_NombreDemasiadoLargo() {
        // Arrange
        turno.setNombre("A".repeat(51)); // 51 caracteres

        // Assert
        assertThrows(RuntimeException.class, () -> turnoService.validarTurno(turno));
    }

    /**
     * Prueba que verifica la validación de un turno sin fecha de inicio.
     * <p>
     * Asegura que se lanza RuntimeException.
     * </p>
     */
    @Test
    public void validarTurno_SinFechaHoraInicio() {
        // Arrange
        turno.setFechaHoraInicio(null);

        // Assert
        assertThrows(RuntimeException.class, () -> turnoService.validarTurno(turno));
    }

    /**
     * Prueba que verifica la validación de un turno sin fecha de fin.
     * <p>
     * Asegura que se lanza RuntimeException.
     * </p>
     */
    @Test
    public void validarTurno_SinFechaHoraFin() {
        // Arrange
        turno.setFechaHoraFin(null);

        // Assert
        assertThrows(RuntimeException.class, () -> turnoService.validarTurno(turno));
    }

    /**
     * Prueba que verifica la validación de un turno con fecha de inicio posterior a la fecha de fin.
     * <p>
     * Asegura que se lanza RuntimeException.
     * </p>
     */
    @Test
    public void validarTurno_FechaInicioPosteriorAFin() {
        // Arrange
        turno.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        turno.setFechaHoraFin(LocalDateTime.now());

        // Assert
        assertThrows(RuntimeException.class, () -> turnoService.validarTurno(turno));
    }
}