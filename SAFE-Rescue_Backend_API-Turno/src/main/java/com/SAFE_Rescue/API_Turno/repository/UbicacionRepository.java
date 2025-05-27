package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

}
