package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entidad que representa una ubicación en el sistema.
 * Contiene información sobre la composición y estado de la ubicación.
 */
@Entity
@Table(name = "ubicacion")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ubicacion {

    /**
     * Identificador único de la ubicación en el sistema.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la ubicación", example = "1")
    private Integer id;

    /**
     * Nombre de la calle o avenida.
     * Ejemplo: "Av. Libertador Bernardo O'Higgins".
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Nombre de la calle o avenida", example = "Av. Libertador Bernardo O'Higgins", required = true, maxLength = 50)
    private String calle;

    /**
     * Número de la dirección.
     * Debe ser un valor positivo.
     * Ejemplo: 1234.
     */
    @Column(length = 5, nullable = false)
    @Schema(description = "Número de la dirección", example = "1234", required = true)
    private Integer numeracion;

    /**
     * Comuna o distrito de la ubicación.
     * Ejemplo: "Santiago Centro".
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Comuna o distrito de la ubicación", example = "Santiago Centro", required = true, maxLength = 50)
    private String comuna;

    /**
     * Región o estado/provincia.
     * Ejemplo: "Región Metropolitana".
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Región o estado/provincia", example = "Región Metropolitana", required = true, maxLength = 50)
    private String region;
}