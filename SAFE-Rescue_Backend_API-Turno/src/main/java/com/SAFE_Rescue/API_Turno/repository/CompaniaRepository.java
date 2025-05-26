package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.modelo.UbicacionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Compania} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 *
 * <p>Esta interfaz extiende {@link JpaRepository} que proporciona métodos estándar para:
 * <ul>
 *   <li>Crear, leer, actualizar y eliminar compañías</li>
 *   <li>Paginación y ordenación de resultados</li>
 *   <li>Operaciones de búsqueda básicas</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * // Inyectar el repositorio
 * @Autowired
 * private CompaniaRepository companiaRepository;
 *
 * // Guardar una nueva compañía
 * companiaRepository.save(nuevaCompania);
 *
 * // Buscar compañía por ID
 * Optional<Compania> encontrada = companiaRepository.findById(1L);
 * }
 * </pre>
 */
@Repository
public interface CompaniaRepository extends JpaRepository<Compania, Long> {

    /**
     * Busca una compañía por su nombre exacto.
     * @param nombre Nombre de la compañía a buscar (debe coincidir exactamente)
     * @return La compañía encontrada o {@code null} si no existe
     */
    Compania findByNombre(String nombre);

    /**
     * Verifica si existe una compañía con el nombre especificado.
     * @param nombre Nombre a verificar
     * @return {@code true} si existe una compañía con ese nombre, {@code false} en caso contrario
     */
    boolean existsByNombre(String nombre);

}
