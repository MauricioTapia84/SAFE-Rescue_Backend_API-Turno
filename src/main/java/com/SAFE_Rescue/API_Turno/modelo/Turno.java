package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa un turno de trabajo en el sistema de bomberos.
 * <p>
 * Define los periodos de tiempo asignados para las operaciones de los equipos,
 * incluyendo horarios de inicio y fin, así como la duración calculada.
 * </p>
 */
@Entity
@Table(name = "turno")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Turno {

    /**
     * Identificador único autoincremental del turno.
     * <p>
     * Se genera automáticamente mediante la estrategia IDENTITY de la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Nombre descriptivo del turno.
     * <p>
     * Restricciones:
     * </p>
     * <ul>
     *   <li>Máximo 50 caracteres</li>
     *   <li>No puede ser nulo</li>
     *   <li>Se almacena en la columna 'nombre_turno'</li>
     * </ul>
     *
     * <p>Ejemplos: "Turno Mañana", "Turno Noche", "Turno Fin de Semana"</p>
     */
    @Column(name = "nombre_turno", length = 50, nullable = false)
    private String nombre;

    /**
     * Fecha y hora de inicio del turno.
     * <p>
     * Debe ser anterior a fechaHoraFin y no puede ser nulo.
     * </p>
     * <p>Formato esperado: YYYY-MM-DDTHH:MM:SS</p>
     */
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    /**
     * Fecha y hora de finalización del turno.
     * <p>
     * Debe ser posterior a fechaHoraInicio y no puede ser nulo.
     * </p>
     * <p>Formato esperado: YYYY-MM-DDTHH:MM:SS</p>
     */
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    /**
     * Duración del turno en horas.
     * <p>
     * Restricciones:
     * </p>
     * <ul>
     *   <li>Valor numérico positivo</li>
     *   <li>Máximo 2 dígitos (99 horas máximo)</li>
     *   <li>No puede ser nulo</li>
     * </ul>
     *
     * <p>Nota: Considerar calcular automáticamente basado en fechaHoraInicio y fechaHoraFin</p>
     */
    @Column(length = 2, nullable = false)
    private Long duracion;

}
