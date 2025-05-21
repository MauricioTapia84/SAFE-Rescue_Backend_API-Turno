package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecursoDTO {

    private Long id;
    private String nombre;
    private String tipoRecurso;
    private int cantidad;
    private boolean estado;

}
