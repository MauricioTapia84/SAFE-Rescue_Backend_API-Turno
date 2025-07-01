package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.modelo.Ubicacion;
import com.SAFE_Rescue.API_Turno.repository.CompaniaRepository;
import com.SAFE_Rescue.API_Turno.repository.UbicacionRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio CompaniaService.
 * <p>
 * Esta clase verifica la funcionalidad de los métodos dentro de CompaniaService,
 * incluyendo operaciones CRUD, validaciones y asignaciones.
 * </p>
 */
@SpringBootTest
public class CompaniaServiceTest {

    @Autowired
    private CompaniaService companiaService;

    @MockitoBean
    private CompaniaRepository companiaRepository;

    @MockitoBean
    private UbicacionRepository ubicacionRepository;

    private Faker faker;
    private Compania compania;
    private Integer id;

    /**
     * Configura el entorno de pruebas antes de cada prueba.
     * <p>
     * Inicializa Faker y crea una instancia de Compania para las pruebas.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        compania = new Compania(1, faker.company().name(),
                new Ubicacion(1, faker.address().streetName(),
                        faker.number().numberBetween(1, 9999),
                        faker.address().city(),
                        faker.address().state()));
        id = 1;
    }

    /**
     * Prueba que verifica la obtención de todas las companias.
     * <p>
     * Asegura que el servicio devuelve la lista correcta de companias.
     * </p>
     */
    @Test
    public void findAllTest() {
        // Arrange
        when(companiaRepository.findAll()).thenReturn(List.of(compania));

        // Act
        List<Compania> companias = companiaService.findAll();

        // Assert
        assertNotNull(companias);
        assertEquals(1, companias.size());
        assertEquals(compania.getNombre(), companias.get(0).getNombre());
    }

    /**
     * Prueba que verifica la búsqueda de una compania por su ID.
     * <p>
     * Asegura que se encuentra la compania correcta.
     * </p>
     */
    @Test
    public void findByIdTest() {
        // Arrange
        when(companiaRepository.findById(id)).thenReturn(Optional.of(compania));

        // Act
        Compania encontrada = companiaService.findByID(id);

        // Assert
        assertNotNull(encontrada);
        assertEquals(id, encontrada.getId());
    }

    /**
     * Prueba que verifica la creación y guardado de una nueva compania.
     * <p>
     * Asegura que la compania se guarda correctamente en el repositorio.
     * </p>
     */
    @Test
    public void saveTest() {
        // Arrange
        when(ubicacionRepository.save(compania.getUbicacion())).thenReturn(compania.getUbicacion());
        when(companiaRepository.save(compania)).thenReturn(compania);

        // Act
        Compania guardado = companiaService.save(compania);

        // Assert
        assertNotNull(guardado);
        assertEquals(compania.getNombre(), guardado.getNombre());
        verify(companiaRepository, times(1)).save(compania);
    }

    /**
     * Prueba que verifica la actualización de una compania existente.
     * <p>
     * Asegura que la compania se actualiza correctamente.
     * </p>
     */
    @Test
    public void updateTest() {
        // Arrange
        Compania companiaExistente = new Compania(1, faker.company().name(),
                new Ubicacion(1, faker.address().streetName(),
                        faker.number().numberBetween(1, 9999),
                        faker.address().city(),
                        faker.address().state()));
        when(companiaRepository.findById(id)).thenReturn(Optional.of(companiaExistente));
        when(companiaRepository.save(companiaExistente)).thenReturn(companiaExistente);

        // Act
        Compania actualizada = companiaService.update(compania, id);

        // Assert
        assertNotNull(actualizada);
        assertEquals(compania.getNombre(), actualizada.getNombre());
        verify(companiaRepository, times(1)).save(companiaExistente);
    }

    /**
     * Prueba que verifica la eliminación de una compania.
     * <p>
     * Asegura que la compania se elimina correctamente del repositorio.
     * </p>
     */
    @Test
    public void deleteTest() {
        // Arrange
        when(companiaRepository.existsById(id)).thenReturn(true);
        doNothing().when(companiaRepository).deleteById(id);

        // Act
        companiaService.delete(id);

        // Assert
        verify(companiaRepository, times(1)).deleteById(id);
    }

    /**
     * Prueba que verifica la validación de una compania.
     * <p>
     * Asegura que una compania válida no lanza excepciones.
     * </p>
     */
    @Test
    public void validarCompaniaTest() {
        // Act & Assert
        assertDoesNotThrow(() -> companiaService.validarCompania(compania));
    }

    /**
     * Prueba que verifica la asignación de una ubicación a una compania.
     * <p>
     * Asegura que se asigna correctamente y no lanza excepciones.
     * </p>
     */
    @Test
    public void asignarUbicacionTest() {
        // Arrange
        Ubicacion ubicacion = new Ubicacion(1, faker.address().streetName(),
                faker.number().numberBetween(1, 9999),
                faker.address().city(),
                faker.address().state());
        compania.setUbicacion(ubicacion);
        when(companiaRepository.findById(id)).thenReturn(Optional.of(compania));
        when(ubicacionRepository.findById(1)).thenReturn(Optional.of(ubicacion));

        // Act
        assertDoesNotThrow(() -> companiaService.asignarUbicacion(id, 1));

        // Assert
        assertEquals(ubicacion, compania.getUbicacion());
        verify(companiaRepository).save(compania);
    }

    // ERRORES

    /**
     * Prueba que verifica la búsqueda por ID cuando la compania no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void findByIdTest_CompaniaNoExiste() {
        // Arrange
        when(companiaRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> companiaService.findByID(id));
    }

    /**
     * Prueba que verifica el intento de actualización de una compania que no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void testUpdate_CompaniaNoExistente() {
        // Arrange
        when(companiaRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> companiaService.update(new Compania(), id));
    }

    /**
     * Prueba que verifica la eliminación de una compania que no existe.
     * <p>
     * Asegura que se lanza la excepción correspondiente.
     * </p>
     */
    @Test
    public void testDelete_CompaniaNoExistente() {
        // Arrange
        when(companiaRepository.existsById(id)).thenReturn(false);

        // Assert
        assertThrows(NoSuchElementException.class, () -> companiaService.delete(id));
    }

    /**
     * Prueba que verifica la validación de una compania con nombre vacío.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarCompania_NombreVacio() {
        // Arrange
        compania.setNombre("");

        // Assert
        assertThrows(IllegalArgumentException.class, () -> companiaService.validarCompania(compania));
    }

    /**
     * Prueba que verifica la validación de una compania con nombre demasiado largo.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarCompania_NombreDemasiadoLargo() {
        // Arrange
        compania.setNombre("A".repeat(51)); // 51 caracteres

        // Assert
        assertThrows(IllegalArgumentException.class, () -> companiaService.validarCompania(compania));
    }

    /**
     * Prueba que verifica la validación de una compania sin ubicación.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarCompania_SinUbicacion() {
        // Arrange
        compania.setUbicacion(null);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> companiaService.validarCompania(compania));
    }

    /**
     * Prueba la validación de una ubicación con numeración negativa.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarUbicacion_NumeracionNegativa() {
        // Arrange
        Ubicacion ubicacion = new Ubicacion(1, "Calle Falsa", -1, "Comuna", "Región");
        compania.setUbicacion(ubicacion);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> companiaService.validarCompania(compania));
    }

    /**
     * Prueba la validación de una ubicación sin calle.
     * <p>
     * Asegura que se lanza IllegalArgumentException.
     * </p>
     */
    @Test
    public void validarUbicacion_SinCalle() {
        // Arrange
        Ubicacion ubicacion = new Ubicacion(1, null, 1, "Comuna", "Región");
        compania.setUbicacion(ubicacion);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> companiaService.validarCompania(compania));
    }

}