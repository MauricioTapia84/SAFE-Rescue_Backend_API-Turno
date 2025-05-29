package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Equipo} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 *
 * <p>Esta interfaz extiende {@link JpaRepository} que proporciona métodos estándar para:
 * <ul>
 *   <li>Crear, leer, actualizar y eliminar equipos</li>
 *   <li>Paginación y ordenación de resultados</li>
 *   <li>Operaciones de búsqueda básicas</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * // Inyectar el repositorio
 * @Autowired
 * private EquipoRepository equipoRepository;
 *
 * // Guardar un nuevo equipo
 * equipoRepository.save(nuevoEquipo);
 *
 * // Buscar equipo por ID
 * Optional<Equipo> encontrado = equipoRepository.findById(1L);
 * }
 * </pre>
 */
@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    /**
     * Busca equipos por su nombre (búsqueda exacta)
     * @param nombre Nombre del equipo a buscar
     * @return Lista de equipos con el nombre especificado
     */
    List<Equipo> findByNombre(String nombre);

    /**
     * Busca equipos cuyo nombre contenga el texto especificado (búsqueda parcial)
     * @param texto Texto a buscar en los nombres de equipo
     * @return Lista de equipos que coinciden con el criterio
     */
    List<Equipo> findByNombreContaining(String texto);

    /**
     * Busca equipos por estado (activo/inactivo)
     * @param estado Estado de los equipos a buscar (true = activo, false = inactivo)
     * @return Lista de equipos con el estado especificado
     */
    List<Equipo> findByEstado(boolean estado);

    /**
     * Verifica si existe un equipo con el nombre especificado
     * @param nombre Nombre del equipo a verificar
     * @return true si existe al menos un equipo con ese nombre, false en caso contrario
     */
    boolean existsByNombre(String nombre);

}

