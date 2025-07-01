package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entidad que representa un recurso en el sistema.
 * Contiene información sobre la composición y estado del recurso.
 */
@Entity
@Table(name = "recurso")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Recurso {

    /**
     * Identificador único del recurso en el sistema.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del recurso", example = "1")
    private int id;

    /**
     * Nombre descriptivo del recurso.
     * Ejemplos: "Camión bomba", "Equipo de rescate", "Botiquín primeros auxilios".
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 100 caracteres.
     */
    @Column(length = 100, nullable = false)
    @Schema(description = "Nombre del recurso", example = "Camión bomba", required = true, maxLength = 100)
    private String nombre;

    /**
     * Categoría o clasificación del recurso.
     * Ejemplos: "VEHICULO", "EQUIPO_MEDICO", "HERRAMIENTA", "MATERIAL".
     * Permite agrupar recursos por tipo para búsquedas y filtrados.
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Categoría del recurso", example = "VEHICULO", required = true, maxLength = 50)
    private String tipoRecurso;

    /**
     * Cantidad disponible de este recurso.
     * Valor entero no negativo (>= 0).
     * Representa unidades disponibles en inventario.
     */
    @Column(nullable = false)
    @Schema(description = "Cantidad disponible del recurso", example = "10", minimum = "0")
    private int cantidad;

}