package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.BomberoDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BomberoDTORepository {
    private List<BomberoDTO> ListaBomberos = new ArrayList<>();

    public List<BomberoDTO> obtenerBomberos() {
        return ListaBomberos;
    }

    public void agregarBombero(BomberoDTO bombero) {
        ListaBomberos.add(bombero);
    }
}
