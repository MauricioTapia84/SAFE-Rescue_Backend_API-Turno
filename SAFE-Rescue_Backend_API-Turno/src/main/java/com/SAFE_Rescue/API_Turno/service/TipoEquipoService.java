package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Turno.repository.TipoEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para gestionar operaciones relacionadas con tipos de equipo de bomberos.
 * Proporciona métodos para CRUD de tipos de equipo y validación de reglas de negocio.
 */
@Service
public class TipoEquipoService {

    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;

    /**
     * Obtiene todos los tipos de equipo registrados.
     * @return Lista de todos los tipos de equipo
     */
    public List<TipoEquipo> findAll() {
        return tipoEquipoRepository.findAll();
    }

    /**
     * Busca un tipo de equipo por su ID.
     * @param id Identificador único del tipo de equipo
     * @return El tipo de equipo encontrado
     * @throws NoSuchElementException Si no se encuentra el tipo de equipo
     */
    public TipoEquipo findByID(long id) {
        return tipoEquipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de equipo no encontrado con ID: " + id));
    }

    /**
     * Guarda un nuevo tipo de equipo.
     * @param tipoEquipo Tipo de equipo a guardar
     * @return Tipo de equipo guardado
     * @throws IllegalArgumentException Si el tipo de equipo no pasa las validaciones
     */
    public TipoEquipo save(TipoEquipo tipoEquipo) {
        validarTipoEquipo(tipoEquipo);
        return tipoEquipoRepository.save(tipoEquipo);
    }

    /**
     * Actualiza un tipo de equipo existente.
     * @param tipoEquipo Datos actualizados del tipo de equipo
     * @param id ID del tipo de equipo a actualizar
     * @return Tipo de equipo actualizado
     * @throws NoSuchElementException Si no se encuentra el tipo de equipo
     * @throws IllegalArgumentException Si los datos no pasan las validaciones
     */
    public TipoEquipo update(TipoEquipo tipoEquipo, long id) {
        TipoEquipo tipoExistente = tipoEquipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de equipo no encontrado con ID: " + id));

        if (tipoEquipo.getNombre() != null) {
            validarNombreTipoEquipo(tipoEquipo.getNombre());
            tipoExistente.setNombre(tipoEquipo.getNombre());
        }

        return tipoEquipoRepository.save(tipoExistente);
    }

    /**
     * Elimina un tipo de equipo por su ID.
     * @param id ID del tipo de equipo a eliminar
     * @throws NoSuchElementException Si no se encuentra el tipo de equipo
     */
    public void delete(long id) {
        if (!tipoEquipoRepository.existsById(id)) {
            throw new NoSuchElementException("Tipo de equipo no encontrado con ID: " + id);
        }
        tipoEquipoRepository.deleteById(id);
    }

    /**
     * Valida los datos de un tipo de equipo.
     * @param tipoEquipo Tipo de equipo a validar
     * @throws IllegalArgumentException Si el tipo de equipo no cumple con las reglas de validación
     */
    private void validarTipoEquipo(TipoEquipo tipoEquipo) {
        if (tipoEquipo.getNombre() == null || tipoEquipo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo de equipo es requerido");
        }
        validarNombreTipoEquipo(tipoEquipo.getNombre());
    }

    /**
     * Valida el nombre de un tipo de equipo.
     * @param nombre Nombre a validar
     * @throws IllegalArgumentException Si el nombre excede el límite de caracteres
     */
    private void validarNombreTipoEquipo(String nombre) {
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder los 50 caracteres");
        }
    }
}