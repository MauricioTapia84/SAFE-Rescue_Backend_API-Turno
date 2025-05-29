package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link TipoEquipo} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 *
 * <p>Esta interfaz extiende {@link JpaRepository} que proporciona métodos estándar para:
 * <ul>
 *   <li>Crear, leer, actualizar y eliminar tipos de equipo</li>
 *   <li>Paginación y ordenación de resultados</li>
 *   <li>Operaciones de búsqueda básicas</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * // Inyectar el repositorio
 * @Autowired
 * private TipoEquipoRepository tipoEquipoRepository;
 *
 * // Guardar un nuevo tipo de equipo
 * tipoEquipoRepository.save(nuevoTipoEquipo);
 *
 * // Buscar tipo de equipo por ID
 * Optional<TipoEquipo> encontrado = tipoEquipoRepository.findById(1L);
 * }
 * </pre>
 */
@Repository
public interface TipoEquipoRepository extends JpaRepository<TipoEquipo, Long> {

    /**
     * Busca un tipo de equipo por su nombre exacto (case-sensitive).
     * @param nombre Nombre exacto del tipo de equipo a buscar
     * @return Optional conteniendo el tipo de equipo si es encontrado
     */
    Optional<TipoEquipo> findByNombre(String nombre);

    /**
     * Busca tipos de equipo cuyo nombre contenga el texto especificado (búsqueda insensible a mayúsculas).
     * @param texto Texto a buscar en los nombres de tipos de equipo
     * @return Lista de tipos de equipo que coinciden con el criterio
     */
    List<TipoEquipo> findByNombreContainingIgnoreCase(String texto);

    /**
     * Verifica si existe un tipo de equipo con el nombre especificado.
     * @param nombre Nombre del tipo de equipo a verificar
     * @return true si existe al menos un tipo de equipo con ese nombre, false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
