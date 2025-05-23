package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.RecursoDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RecursoDTORepository {

    private List<RecursoDTO> ListaRecursos = new ArrayList<>();

    public List<RecursoDTO> obtenerRecursos() {
        return ListaRecursos;
    }

    public void agregarRecurso(RecursoDTO recurso) {
        ListaRecursos.add(recurso);
    }
}

