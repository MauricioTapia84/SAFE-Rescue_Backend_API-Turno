package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Entidad que representa un equipo de bomberos en el sistema.
 * Contiene información sobre la composición y estado del equipo.
 */
@Entity
@Table(name = "equipo") // Nombre de la tabla en la base de datos
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
@Data // Genera getters, setters, toString, equals y hashCode
public class Equipo {

    /**
     * Identificador único del equipo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental
    @Schema(description = "Identificador único del equipo", example = "1")
    private Integer id;

    /**
     * Nombre del equipo (máximo 50 caracteres).
     */
    @Column(name = "nombre_equipo", length = 50, nullable = false)
    @Schema(description = "Nombre del equipo", example = "Equipo A", required = true, maxLength = 50)
    private String nombre;

    /**
     * Cantidad de miembros en el equipo (hasta 99).
     */
    @Column(name = "cantidad_miembros", length = 2, nullable = true)
    @Schema(description = "Cantidad de miembros en el equipo", example = "5", minimum = "0", maximum = "99")
    private Integer cantidadMiembros;

    /**
     * Estado actual del equipo (activo/inactivo).
     */
    @Column(nullable = false)
    @Schema(description = "Estado del equipo", example = "true")
    private boolean estado;

    /**
     * Nombre del líder del equipo (máximo 50 caracteres).
     */
    @Column(name = "nombre_lider", length = 50, nullable = true)
    @Schema(description = "Nombre del líder del equipo", example = "Juan Pérez", maxLength = 50)
    private String lider;

    /**
     * Lista de vehículos.
     * Relación uno-a-muchos con la entidad Vehiculo.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vehiculos")
    @Schema(description = "Lista de vehículos asignados al equipo")
    private List<Vehiculo> vehiculos;

    /**
     * Lista de personal.
     * Relación uno-a-muchos con la entidad Bombero.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal")
    @Schema(description = "Lista de bomberos asignados al equipo")
    private List<Bombero> personal;

    /**
     * Lista de recursos.
     * Relación uno-a-muchos con la entidad Recurso.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recursos")
    @Schema(description = "Lista de recursos asignados al equipo")
    private List<Recurso> recursos;

    /**
     * Turno asignado al equipo.
     * Relación muchos-a-uno con la entidad Turno.
     */
    @ManyToOne
    @JoinColumn(name = "turno_id", referencedColumnName = "id")
    @Schema(description = "Turno asignado al equipo")
    private Turno turno;

    /**
     * Compañía a la que pertenece el equipo.
     * Relación muchos-a-uno con la entidad Compania.
     */
    @ManyToOne
    @JoinColumn(name = "compania_id", referencedColumnName = "id")
    @Schema(description = "Compañía a la que pertenece el equipo")
    private Compania compania;

    /**
     * Tipo de equipo (especialización).
     * Relación muchos-a-uno con la entidad TipoEquipo.
     */
    @ManyToOne
    @JoinColumn(name = "tipo_equipo_id", referencedColumnName = "id")
    @Schema(description = "Tipo de equipo asignado")
    private TipoEquipo tipoEquipo;
}