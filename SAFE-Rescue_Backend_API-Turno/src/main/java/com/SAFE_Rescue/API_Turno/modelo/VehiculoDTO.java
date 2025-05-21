package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehiculoDTO {

    private Long id;
    private String marca;
    private String patente;
    private String conductor;
    private boolean estado;

}
