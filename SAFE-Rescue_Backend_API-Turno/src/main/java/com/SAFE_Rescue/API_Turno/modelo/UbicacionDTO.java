package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UbicacionDTO {

    private Long id;
    private String calle;
    private int numeracion;
    private String comuna;
    private String region;

}
