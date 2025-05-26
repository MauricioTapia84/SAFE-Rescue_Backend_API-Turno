package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa una ubicación geográfica.
 * Contiene información estructurada de direcciones para uso en el sistema.
 *
 * <p>Se utiliza para transferir datos de ubicación entre capas de la aplicación
 * sin exponer la entidad completa del modelo de dominio.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {

    /**
     * Identificador único de la ubicación en el sistema
     */
    private Long id;

    /**
     * Nombre de la calle o avenida
     * Ejemplo: "Av. Libertador Bernardo O'Higgins"
     */
    private String calle;

    /**
     * Número de la dirección
     * Debe ser un valor positivo
     * Ejemplo: 1234
     */
    private int numeracion;

    /**
     * Comuna o distrito de la ubicación
     * Ejemplo: "Santiago Centro"
     */
    private String comuna;

    /**
     * Región o estado/provincia
     * Ejemplo: "Región Metropolitana"
     */
    private String region;
}