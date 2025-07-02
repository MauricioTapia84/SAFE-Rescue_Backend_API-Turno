package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

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
     * Identificador único de la compañía.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la compañía", example = "1")
    private Integer id;

    /**
     * Nombre de la compañía (debe ser único).
     * Restricciones:
     * - Máximo 8 caracteres
     * - No puede ser nulo
     */
    @Column(unique = true, length = 50, nullable = false)
    @Schema(description = "Nombre de la compañía", example = "Bomberos", required = true, maxLength = 50)
    private String nombre;

    /**
     * Ubicación asignada a la compañía.
     * Relación uno-a-uno con la entidad Ubicacion.
     */
    @OneToOne
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
    @Schema(description = "Ubicación asignada a la compañía")
    private Ubicacion ubicacion;

}