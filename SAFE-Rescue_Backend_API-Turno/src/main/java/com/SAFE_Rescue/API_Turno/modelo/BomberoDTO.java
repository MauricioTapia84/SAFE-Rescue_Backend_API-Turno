package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BomberoDTO {

    private Long id;
    private String nombre;
    private String a_paterno;
    private String a_materno;

}
