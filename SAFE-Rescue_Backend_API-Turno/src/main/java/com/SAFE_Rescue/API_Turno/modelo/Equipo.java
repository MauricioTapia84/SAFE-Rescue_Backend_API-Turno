package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "equipo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre_equipo", length = 50, nullable = false)
    private String nombre;

    @Column(name= "cantidad_miembros",length = 2, nullable = true)
    private  int cantidadMiembros;

    @Column(nullable = false)
    private boolean estado;

    @Column(name = "nombre_lider",length = 50, nullable = true)
    private String lider;

    private List<VehiculoDTO> vehiculosAsignados;

    private List<BomberoDTO> personal;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "turno_id", referencedColumnName = "id")
    private Turno turno;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compania_id", referencedColumnName = "id")
    private Compania compania;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tipo_equipo_id", referencedColumnName = "id")
    private TipoEquipo tipoEquipo;


}
