package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entidad que representa un vehículo en el sistema.
 * Contiene información sobre la composición y estado del vehículo.
 */
@Entity
@Table(name = "vehiculo")
@NoArgsConstructor
@AllArgsConstructor
@Data // Lombok: Genera constructor con todos los campos
public class Vehiculo {

    /**
     * ID único del vehículo en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del vehículo", example = "1")
    private Integer id;

    /**
     * Marca o fabricante del vehículo (ej: "Toyota", "Ford").
     * Debe ser un valor no nulo y con una longitud razonable.
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Marca o fabricante del vehículo", example = "Toyota", required = true, maxLength = 50)
    private String marca;

    /**
     * Modelo del vehículo.
     * Debe ser un valor no nulo y con una longitud razonable.
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Modelo del vehículo", example = "Hilux", required = true, maxLength = 50)
    private String modelo;

    /**
     * Patente o matrícula del vehículo (ej: "AB123CD").
     * Identificador único legal del vehículo.
     */
    @Column(length = 6, nullable = false)
    @Schema(description = "Patente o matrícula del vehículo", example = "AB123C", required = true, maxLength = 6)
    private String patente;

    /**
     * Nombre del conductor asignado al vehículo.
     * Puede ser null si el vehículo no tiene conductor asignado.
     */
    @Column(length = 50, nullable = true)
    @Schema(description = "Nombre del conductor asignado al vehículo", example = "Juan Pérez", maxLength = 50)
    private String conductor;

    /**
     * Nombre descriptivo del estado del vehículo.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Estado del vehículo", example = "Operativo", required = true, maxLength = 50)
    private String estado;
}