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

    /**
     * Lista de vehículos asignados al equipo, representados como DTOs.
     * <p>
     * Contiene los datos de los vehículos mapeados desde el servicio externo de flota.
     * Esta información no se persiste directamente en la base de datos local,
     * sino que se obtiene bajo demanda desde el API correspondiente.
     * </p>
     *
     * @see VehiculoDTO
     */
    @ElementCollection
    @CollectionTable(name = "equipo_vehiculos", joinColumns = @JoinColumn(name = "equipo_id"))
    @Column(name = "vehiculo_id")
    private List<Long> vehiculosAsignadosIds;

    /**
     * Personal asignado al equipo, representado como DTOs de bomberos.
     * <p>
     * Lista de bomberos asociados al equipo, obtenida desde el servicio de personal
     * y mapeada a través de {@link BomberoDTO}. Estos datos se actualizan periódicamente
     * desde el sistema central de recursos humanos.
     * </p>
     *
     * @see BomberoDTO
     */
    @ElementCollection
    @CollectionTable(name = "equipo_personal", joinColumns = @JoinColumn(name = "equipo_id"))
    @Column(name = "bombero_id")
    private List<Long> personalIds;

    /**
     * Recursos disponibles para el equipo, representados como DTOs.
     * <p>
     * Inventario de recursos asignados al equipo, obtenido desde el servicio de logística
     * y mapeado mediante {@link RecursoDTO}. La lista se actualiza al realizar operaciones
     * de check-in/check-out de recursos.
     * </p>
     *
     * @see RecursoDTO
     */
    @ElementCollection
    @CollectionTable(name = "equipo_recursos", joinColumns = @JoinColumn(name = "equipo_id"))
    @Column(name = "recurso_id")
    private List<Long> recursosDisponiblesIds;

    /**
     * Turno asignado al equipo
     * Relación uno-a-uno con cascada para operaciones
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "turno_id", referencedColumnName = "id")
    private Turno turno;

    /**
     * Compañía a la que pertenece el equipo
     * Relación uno-a-uno con cascada para operaciones
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compania_id", referencedColumnName = "id")
    private Compania compania;

    /**
     * Tipo de equipo (especialización)
     * Relación uno-a-uno con cascada para operaciones
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tipo_equipo_id", referencedColumnName = "id")
    private TipoEquipo tipoEquipo;

    // Campos transient para datos completos cuando sea necesario
    @Transient
    private List<VehiculoDTO> vehiculosAsignados;

    @Transient
    private List<BomberoDTO> personal;

    @Transient
    private List<RecursoDTO> recursosDisponibles;
}
