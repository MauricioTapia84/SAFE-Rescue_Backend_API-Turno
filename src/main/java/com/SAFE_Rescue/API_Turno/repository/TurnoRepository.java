package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad {@link Turno} que proporciona operaciones CRUD básicas
 * y acceso a datos utilizando Spring Data JPA.
 */
@Repository
public interface TurnoRepository extends JpaRepository<Turno, Integer> {

}