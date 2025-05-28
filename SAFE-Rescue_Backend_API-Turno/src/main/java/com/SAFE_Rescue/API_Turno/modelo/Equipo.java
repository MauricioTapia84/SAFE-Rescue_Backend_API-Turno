package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
     * Identificador único del equipo
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental
    private int id;

    /**
     * Nombre del equipo (máximo 50 caracteres)
     */
    @Column(name = "nombre_equipo", length = 50, nullable = false)
    private String nombre;

    /**
     * Cantidad de miembros en el equipo (hasta 99)
     */
    @Column(name = "cantidad_miembros", length = 2, nullable = true)
    private Integer cantidadMiembros;

    /**
     * Estado actual del equipo (activo/inactivo)
     */
    @Column(nullable = false)
    private boolean estado;

    /**
     * Nombre del líder del equipo (máximo 50 caracteres)
     */
    @Column(name = "nombre_lider", length = 50, nullable = true)
    private String lider;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "equipo_id")
    private List<Vehiculo> vehiculos;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "equipo_id")
    private List<Bombero> personal;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "equipo_id")
    private List<Recurso> recursos;

    /**
     * Turno asignado al equipo
     * Relación uno-a-muchos
     */
    @ManyToOne
    @JoinColumn(name = "turno_id", referencedColumnName = "id")
    private Turno turno;

    /**
     * Compañía a la que pertenece el equipo
     * Relación uno-a-muchos
     */
    @ManyToOne
    @JoinColumn(name = "compania_id", referencedColumnName = "id")
    private Compania compania;

    /**
     * Tipo de equipo (especialización)
     * Relación uno-a-muchos
     */
    @ManyToOne
    @JoinColumn(name = "tipo_equipo_id", referencedColumnName = "id")
    private TipoEquipo tipoEquipo;

}
