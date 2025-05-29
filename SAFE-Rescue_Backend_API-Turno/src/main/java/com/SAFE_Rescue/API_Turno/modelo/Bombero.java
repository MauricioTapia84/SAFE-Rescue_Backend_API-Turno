package com.SAFE_Rescue.API_Turno.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "bombero")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Bombero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(name="a_paterno",length = 50, nullable = false)
    private String aPaterno;

    @Column(name="a_materno",length = 50, nullable = false)
    private String aMaterno;

    @Column(unique = true, length = 9, nullable = false)
    private Long telefono;
}