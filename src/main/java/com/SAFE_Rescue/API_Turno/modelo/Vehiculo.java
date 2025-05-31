package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entidad que representa un Vehiculo en el sistema.
 * Contiene información sobre la composición y estado del vehiculo
 */
@Entity
@Table(name = "vehiculo")
@NoArgsConstructor
@AllArgsConstructor
@Data// Lombok: Genera constructor con todos los campos
public class Vehiculo {

    /**
     * ID único del vehículo en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Marca o fabricante del vehículo (ej: "Toyota", "Ford").
     * Debe ser un valor no nulo y con una longitud razonable.
     */
    @Column(length = 50, nullable = false)
    private String marca;

    /**
     * Modelo
     * Debe ser un valor no nulo y con una longitud razonable.
     */
    @Column(length = 50, nullable = false)
    private String modelo;

    /**
     * Patente o matrícula del vehículo (ej: "AB123CD").
     * Identificador único legal del vehículo.
     */
    @Column(length = 6, nullable = false)
    private String patente;

    /**
     * Nombre del conductor asignado al vehículo.
     * Puede ser null si el vehículo no tiene conductor asignado.
     */
    @Column(length = 50, nullable = false)
    private String conductor;

    /**
     * Estado actual del vehículo (true = activo/disponible, false = inactivo/no disponible).
     * Por defecto, debería ser true (activo) al crear un nuevo vehículo.
     */
    @Column(length = 50, nullable = false)
    private String estado;
}