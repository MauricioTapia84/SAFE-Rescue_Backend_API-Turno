package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.Bombero;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BomberoRepository extends JpaRepository<Bombero, Long>{


}



