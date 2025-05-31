package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Equipo} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 */
@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {


}

