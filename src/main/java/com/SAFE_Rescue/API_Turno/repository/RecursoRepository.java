package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Recurso} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 */
@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
}
