package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entidad que representa un bombero en el sistema.
 * Contiene información sobre la composición y estado del bombero.
 */
@Entity
@Table(name = "bombero")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Bombero {

    /**
     * Identificador único del bombero.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del bombero")
    private int id;

    /**
     * Nombre descriptivo del bombero.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Nombre del bombero", example = "Juan", required = true, maxLength = 50)
    private String nombre;

    /**
     * Apellido paterno descriptivo del bombero.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(name="a_paterno", length = 50, nullable = false)
    @Schema(description = "Apellido paterno del bombero", example = "Pérez", required = true, maxLength = 50)
    private String aPaterno;

    /**
     * Apellido materno descriptivo del bombero.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(name="a_materno", length = 50, nullable = false)
    @Schema(description = "Apellido materno del bombero", example = "García", required = true, maxLength = 50)
    private String aMaterno;

    /**
     * Teléfono disponible del bombero.
     * Valor entero no negativo (>= 0).
     * Representa unidades disponibles en inventario.
     */
    @Column(unique = true, length = 9, nullable = false)
    @Schema(description = "Teléfono del bombero", example = "987654321", required = true)
    private Integer telefono;
}