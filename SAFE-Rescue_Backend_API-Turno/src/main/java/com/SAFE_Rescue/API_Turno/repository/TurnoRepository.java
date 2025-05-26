package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad {@link Turno} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 *
 * <p>Esta interfaz extiende {@link JpaRepository} que proporciona métodos estándar para:
 * <ul>
 *   <li>Crear, leer, actualizar y eliminar turnos</li>
 *   <li>Paginación y ordenación de resultados</li>
 *   <li>Operaciones de búsqueda básicas</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * // Inyectar el repositorio
 * @Autowired
 * private TurnoRepository turnoRepository;
 *
 * // Guardar un nuevo turno
 * turnoRepository.save(nuevoTurno);
 *
 * // Buscar turno por ID
 * Optional<Turno> encontrado = turnoRepository.findById(1L);
 * }
 * </pre>
 */
@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    /**
     * Busca turnos por nombre exacto (case-sensitive).
     * @param nombre Nombre exacto del turno
     * @return Lista de turnos con el nombre especificado
     */
    List<Turno> findByNombre(String nombre);

    /**
     * Busca turnos cuyo nombre contenga el texto especificado (búsqueda insensible a mayúsculas).
     * @param texto Texto a buscar en los nombres de turno
     * @return Lista de turnos que coinciden con el criterio
     */
    List<Turno> findByNombreContainingIgnoreCase(String texto);

    /**
     * Busca turnos que estén activos en un rango de fechas específico.
     * @param inicio Fecha y hora de inicio del rango
     * @param fin Fecha y hora de fin del rango
     * @return Lista de turnos que se superponen con el rango especificado
     */
    List<Turno> findByFechaHoraInicioLessThanEqualAndFechaHoraFinGreaterThanEqual(
            LocalDateTime fin, LocalDateTime inicio);

    /**
     * Verifica si existe un turno con el nombre especificado.
     * @param nombre Nombre del turno a verificar
     * @return true si existe al menos un turno con ese nombre, false en caso contrario
     */
    boolean existsByNombre(String nombre);

}