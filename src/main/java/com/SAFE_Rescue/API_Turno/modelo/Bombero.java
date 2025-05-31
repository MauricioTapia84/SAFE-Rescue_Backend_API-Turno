package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
     * Identificador único del bombero
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Nombre descriptivo del bombero
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Column(length = 50, nullable = false)
    private String nombre;

    /**
     * Apellido paterno descriptivo del bombero
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Column(name="a_paterno",length = 50, nullable = false)
    private String aPaterno;

    /**
     * Apellido materno descriptivo del bombero
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Column(name="a_materno",length = 50, nullable = false)
    private String aMaterno;

    /**
     * Telefono disponible del bombero
     * Valor entero no negativo (>= 0)
     * Representa unidades disponibles en inventario
     */
    @Column(unique = true, length = 9, nullable = false)
    private Long telefono;
}