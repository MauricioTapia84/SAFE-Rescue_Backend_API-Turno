package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Table(name = "recurso")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Recurso {

    /**
     * Identificador único del recurso en el sistema
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Nombre descriptivo del recurso
     * Ejemplos: "Camión bomba", "Equipo de rescate", "Botiquín primeros auxilios"
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 100 caracteres
     */
    @Column(length = 50, nullable = false)
    private String nombre;

    /**
     * Categoría o clasificación del recurso
     * Ejemplos: "VEHICULO", "EQUIPO_MEDICO", "HERRAMIENTA", "MATERIAL"
     * Permite agrupar recursos por tipo para búsquedas y filtrados
     */
    @Column(length = 50, nullable = false)
    private String tipoRecurso;

    /**
     * Cantidad disponible de este recurso
     * Valor entero no negativo (>= 0)
     * Representa unidades disponibles en inventario
     */
    @Column(unique = true, length = 9, nullable = false)
    private int cantidad;

}