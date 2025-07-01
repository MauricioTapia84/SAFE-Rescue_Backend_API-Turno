package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link TipoEquipo} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 */
@Repository
public interface TipoEquipoRepository extends JpaRepository<TipoEquipo, Integer> {

}
