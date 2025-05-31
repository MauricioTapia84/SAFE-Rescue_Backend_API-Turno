package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una Compañía de bomberos.
 * Contiene información básica de identificación y ubicación.
 */
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
@Data // Genera getters, setters, toString, equals y hashCode
@Entity // Indica que es una entidad persistente
@Table(name = "compania") // Nombre de la tabla en la base de datos
public class Compania {

    /**
     * Identificador único de la compañía
     * Se genera automáticamente mediante estrategia de identidad
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Nombre de la compañía (debe ser único)
     * Restricciones:
     * - Máximo 8 caracteres
     * - No puede ser nulo
     */
    @Column(unique = true, length = 8, nullable = false)
    private String nombre;

    /**
     * ubicacion asignada al equipo
     * Relación uno-a-muchos
     */
    @OneToOne
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
    private Ubicacion ubicacion;

}
