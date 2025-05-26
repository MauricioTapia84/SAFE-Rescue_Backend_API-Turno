package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa un recurso disponible para emergencias.
 * Permite transferir información sobre recursos entre diferentes capas de la aplicación
 * sin exponer la entidad completa del modelo de dominio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursoDTO {

    /**
     * Identificador único del recurso en el sistema
     */
    private Long id;

    /**
     * Nombre descriptivo del recurso
     * Ejemplos: "Camión bomba", "Equipo de rescate", "Botiquín primeros auxilios"
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 100 caracteres
     */
    private String nombre;

    /**
     * Categoría o clasificación del recurso
     * Ejemplos: "VEHICULO", "EQUIPO_MEDICO", "HERRAMIENTA", "MATERIAL"
     * Permite agrupar recursos por tipo para búsquedas y filtrados
     */
    private String tipoRecurso;

    /**
     * Cantidad disponible de este recurso
     * Valor entero no negativo (>= 0)
     * Representa unidades disponibles en inventario
     */
    private int cantidad;

    /**
     * Estado de disponibilidad del recurso
     * true = Disponible para uso
     * false = No disponible (en mantenimiento, agotado, etc.)
     * Por defecto debería inicializarse como true (disponible)
     */
    private boolean estado;
}