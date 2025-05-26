package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa la información básica de un vehículo.
 * Se utiliza para transferir datos entre capas de la aplicación sin exponer el modelo de dominio completo.
 * Contiene información esencial como marca, patente, conductor y estado del vehículo.
 */
@Data // Lombok: Genera getters, setters, toString(), equals(), hashCode()
@NoArgsConstructor // Lombok: Genera constructor sin argumentos
@AllArgsConstructor // Lombok: Genera constructor con todos los campos
public class VehiculoDTO {

    /**
     * ID único del vehículo en la base de datos.
     */
    private Long id;

    /**
     * Marca o fabricante del vehículo (ej: "Toyota", "Ford").
     * Debe ser un valor no nulo y con una longitud razonable.
     */
    private String marca;

    /**
     * Patente o matrícula del vehículo (ej: "AB123CD").
     * Identificador único legal del vehículo.
     */
    private String patente;

    /**
     * Nombre del conductor asignado al vehículo.
     * Puede ser null si el vehículo no tiene conductor asignado.
     */
    private String conductor;

    /**
     * Estado actual del vehículo (true = activo/disponible, false = inactivo/no disponible).
     * Por defecto, debería ser true (activo) al crear un nuevo vehículo.
     */
    private boolean estado;
}