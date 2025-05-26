package com.SAFE_Rescue.API_Turno.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa la información básica de un bombero.
 * Se utiliza para transferir datos entre capas de la aplicación sin exponer el modelo completo.
 */
@Data // Anotación de Lombok para generar automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor // Anotación de Lombok para generar constructor sin argumentos
@AllArgsConstructor // Anotación de Lombok para generar constructor con todos los argumentos
public class BomberoDTO {

    /**
     * Identificador único del bombero en la base de datos.
     */
    private Long id;

    /**
     * Nombre(s) del bombero.
     */
    private String nombre;

    /**
     * Apellido paterno del bombero.
     */
    private String aPaterno;

    /**
     * Apellido materno del bombero.
     */
    private String aMaterno;
}