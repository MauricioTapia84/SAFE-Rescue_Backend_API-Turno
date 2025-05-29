package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa los diferentes tipos de equipos de bomberos en el sistema.
 * <p>
 * Cada tipo de equipo define una categorización particular para los equipos operativos,
 * permitiendo agruparlos por características o funciones específicas.
 * </p>
 *
 * @see Equipo
 */
@Entity
@Table(name = "tipo_equipo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TipoEquipo {

    /**
     * Identificador único autoincremental del tipo de equipo.
     * <p>
     * Se genera automáticamente mediante la estrategia IDENTITY de la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Nombre descriptivo del tipo de equipo.
     * <p>
     * Restricciones:
     * </p>
     * <ul>
     *   <li>Máximo 50 caracteres</li>
     *   <li>No puede ser nulo</li>
     *   <li>Se almacena en la columna 'nombre_tipo'</li>
     * </ul>
     *
     * <p>Ejemplos: "Equipo de Rescate", "Equipo de Primeros Auxilios", "Equipo de Materiales Peligrosos"</p>
     */
    @Column(name = "nombre_tipo", length = 50, nullable = false)
    private String nombre;

}