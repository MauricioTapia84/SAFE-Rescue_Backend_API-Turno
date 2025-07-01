package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Turno.repository.TipoEquipoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TipoEquipoServiceTest {

    @Autowired
    private TipoEquipoService tipoEquipoService;

    @MockitoBean
    private TipoEquipoRepository tipoEquipoRepository;

    private Faker faker;
    private TipoEquipo tipoEquipo;
    private TipoEquipo tipoEquipoNulo;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        tipoEquipo = new TipoEquipo(1, faker.job().position());
        tipoEquipoNulo = new TipoEquipo(1, null);
        id = 1;
    }

    /**
     * Prueba para obtener todos los Tipos de Equipos registrados en el sistema.
     */
    @Test
    public void findAllTest() {
        // Arrange
        when(tipoEquipoRepository.findAll()).thenReturn(List.of(tipoEquipo));

        // Act
        List<TipoEquipo> tiposEquipos = tipoEquipoService.findAll();

        // Assert
        assertNotNull(tiposEquipos);
        assertEquals(1, tiposEquipos.size());
        assertEquals(tipoEquipo.getNombre(),tiposEquipos.get(0).getNombre());
    }

    /**
     * Prueba para buscar un tipo Equipo por su ID.
     */
    @Test
    public void findByIdTest() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.of(tipoEquipo));

        // Act
        TipoEquipo encontrado = tipoEquipoService.findById(id);

        // Assert
        assertNotNull(encontrado);
        assertEquals(id, encontrado.getId());
        assertEquals(tipoEquipo.getNombre(), encontrado.getNombre());
    }

    /**
     * Prueba para guardar un nuevo tipo Equipo en el sistema.
     */
    @Test
    public void saveTest() {
        // Arrange
        when(tipoEquipoRepository.save(tipoEquipo)).thenReturn(tipoEquipo);

        // Act
        TipoEquipo guardado = tipoEquipoService.save(tipoEquipo);

        // Assert
        assertNotNull(guardado);
        assertEquals(tipoEquipo.getNombre(), guardado.getNombre());
        verify(tipoEquipoRepository, times(1)).save(tipoEquipo);
    }

    /**
     * Prueba para actualizar un tipo Equipo existente.
     */
    @Test
    public void updateTest() {
        // Arrange
        TipoEquipo tipoEquipoExistente = new TipoEquipo(id, faker.job().position());
        TipoEquipo tipoEquipoActualizado = new TipoEquipo(id, faker.job().title());
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.of(tipoEquipoExistente));
        when(tipoEquipoRepository.save(tipoEquipoExistente)).thenReturn(tipoEquipoActualizado);

        // Act
        TipoEquipo actualizado = tipoEquipoService.update(tipoEquipoActualizado, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals(tipoEquipoActualizado.getNombre(), actualizado.getNombre());
        assertEquals(id, actualizado.getId());
        verify(tipoEquipoRepository, times(1)).save(tipoEquipoExistente);
    }

    /**
     * Prueba para eliminar un tipo Equipo del sistema.
     */
    @Test
    public void deleteTest() {
        // Arrange
        when(tipoEquipoRepository.existsById(id)).thenReturn(true);
        doNothing().when(tipoEquipoRepository).deleteById(id);

        // Act
        tipoEquipoService.delete(id);

        // Assert
        verify(tipoEquipoRepository, times(1)).deleteById(id);
    }

    /**
     * Prueba para validar un tipo Equipo en el sistema
    */
    @Test
    public void validarTipoEquipoTest() {
        // Arrange
        String nombre = faker.job().position();
        if (nombre.length() > 50) {
            nombre = nombre.substring(0, 50);
        }

        TipoEquipo tipoEquipo = new TipoEquipo(1, nombre);

        // Act & Assert
        assertDoesNotThrow(() -> tipoEquipoService.validarTipoEquipo(tipoEquipo));
    }


    //ERRORES

    /**
     * Prueba para manejar la búsqueda de un tipo Equipo por su ID de un tipo Equipo que no existe.
     */
    @Test
    public void findByIdRolNoEncontradoTest() {
        // Arrange
        when(tipoEquipoRepository.existsById(id)).thenReturn(false);

        // Assert
        assertThrows(NoSuchElementException.class, () -> tipoEquipoService.findById(id));
    }

    /**
     * Prueba para validar los datos al guardar un tipo Equipo.
     */
    @Test
    public void saveRolValidacionTest() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.save(tipoEquipoNulo));
    }

    /**
     * Prueba para manejar la actualización de un tipo Equipo que no existe.
     */
    @Test
    public void updateRolNoEncontradoTest() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> tipoEquipoService.update(tipoEquipo,id));
    }

    /**
     * Prueba para validar los datos al actualizar un tipo Equipo.
     */
    @Test
    public void updateRolValidacionTest() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.of(new TipoEquipo(id,"NombreValido")));

        // Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.update(tipoEquipoNulo,1));
    }


    /**
     * Prueba para manejar la eliminación de un tipo Equipo que no existe.
     */
    @Test
    public void deleteRolNoEncontradoTest() {
        // Arrange
        when(tipoEquipoRepository.existsById(id)).thenReturn(false);

        // Assert
        assertThrows(NoSuchElementException.class, () -> tipoEquipoService.delete(id));
    }

    /**
     * Prueba para validar nombre nulo
     */
    @Test
    public void validarRolNombreNuloTest() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.validarTipoEquipo(tipoEquipoNulo));
    }

    /**
     * Prueba para validar nombre que excede el límite de caracteres
     */
    @Test
    public void validarRolNombreExcedeLimiteTest() {
        // Arrange
        TipoEquipo rolInvalido = new TipoEquipo(1,"EsteNombreEsDemasiadoLargoParaElLimiteDeCincuentaCaracteres");

        // Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.validarTipoEquipo(rolInvalido));
    }
}