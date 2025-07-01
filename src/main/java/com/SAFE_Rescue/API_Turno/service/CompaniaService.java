package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.modelo.Ubicacion;
import com.SAFE_Rescue.API_Turno.repository.CompaniaRepository;
import com.SAFE_Rescue.API_Turno.repository.UbicacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para gestionar operaciones relacionadas con compañías de bomberos.
 * <p>
 * Proporciona métodos para CRUD de compañías, validación de reglas de negocio
 * y comunicación con servicios externos para obtener información de ubicaciones.
 * </p>
 */
@Service
public class CompaniaService {

    // REPOSITORIOS INYECTADOS

    @Autowired private CompaniaRepository companiaRepository;
    @Autowired private UbicacionRepository ubicacionRepository;

    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todas las compañías registradas.
     * @return Lista de todas las compañías
     */
    public List<Compania> findAll() {
        return companiaRepository.findAll();
    }

    /**
     * Busca una compañía por su ID.
     * @param id Identificador único de la compañía
     * @return La compañía encontrada
     * @throws NoSuchElementException Si no se encuentra la compañía
     */
    public Compania findByID(Integer id) {
        return companiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compañía no encontrada con ID: " + id));
    }

    /**
     * Guarda una nueva compañía en el sistema con validación de ubicación.
     * @param compania Compañía a guardar
     * @return Compañía guardada con ID generado
     * @throws IllegalArgumentException Si la compañía no pasa las validaciones
     * @throws RuntimeException Si ocurre un error al guardar o validar la ubicación
     */
    public Compania save(Compania compania) {
        try{

            validarCompania(compania);

            return companiaRepository.save(compania);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar el Compania: " + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado: " + ex.getMessage());
        }
    }

    /**
     * Actualiza una compañía existente con validación de datos.
     * @param compania Datos actualizados de la compañía
     * @param id ID de la compañía a actualizar
     * @return Compañía actualizada
     * @throws NoSuchElementException Si no se encuentra la compañía con el ID especificado
     * @throws IllegalArgumentException Si los datos no pasan las validaciones
     */
    public Compania update(Compania compania, Integer id) {
        Compania antiguaCompania = companiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compañía no encontrada con ID: " + id));

        validarCompania(compania);

        if (compania.getNombre() != null) {
            antiguaCompania.setNombre(compania.getNombre());
        }

        if (compania.getUbicacion() != null) {
            antiguaCompania.setUbicacion(compania.getUbicacion());
        }

        return companiaRepository.save(antiguaCompania);
    }

    /**
     * Elimina una compañía por su ID.
     * @param id ID de la compañía a eliminar
     * @throws NoSuchElementException Si no se encuentra la compañía
     */
    public void delete(Integer id) {
        if (!companiaRepository.existsById(id)) {
            throw new NoSuchElementException("Compañía no encontrada con ID: " + id);
        }
        companiaRepository.deleteById(id);
    }


    //Validaciones

    /**
     * Valida los datos básicos de una compañía.
     * @param compania Compañía a validar
     * @throws IllegalArgumentException Si la compañía no cumple con las reglas de validación
     */
    public void validarCompania(Compania compania) {
        if (compania.getNombre() == null || compania.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la compañía es requerido");
        }
        if (compania.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder los 50 caracteres");
        }

        validarUbicacion(compania.getUbicacion());
    }

    /**
     * Valida los datos de una ubicación.
     * @param ubicacion Ubicación a validar
     * @throws IllegalArgumentException Si la ubicación no cumple con las reglas de validación
     */
    private void validarUbicacion(Ubicacion ubicacion) {

        //numeracion
        if (ubicacion.getNumeracion() <= 0) {
            throw new IllegalArgumentException("La numeración debe ser un número positivo");
        } else {
            if (String.valueOf(ubicacion.getNumeracion()).length()> 5) {
                throw new RuntimeException("El valor de la Numeración excede máximo de caracteres (5)");
            }
        }

        //calle
        if (ubicacion.getCalle() != null) {
            if (ubicacion.getCalle().length() > 50) {
                throw new RuntimeException("El nombre de la calle no puede exceder 50 caracteres");
            }
        }else{
            throw new IllegalArgumentException("El nombre de la calle es requerido");
        }

        //comuna
        if (ubicacion.getComuna() != null) {
            if (ubicacion.getComuna().length() > 50) {
                throw new RuntimeException("El nombre de la comuna no puede exceder 50 caracteres");
            }
        }else{
            throw new IllegalArgumentException("El nombre de la comuna es requerido");
        }

        //region
        if (ubicacion.getRegion() != null) {
            if (ubicacion.getRegion().length() > 50) {
                throw new RuntimeException("El nombre de la Región no puede exceder 50 caracteres");
            }
        }else{
            throw new IllegalArgumentException("El nombre de la Región es requerido");
        }

    }


    // MÉTODOS DE ASIGNACIÓN y OBTENCION DE RELACIONES

    /**
     * Asigna una ubicación a una compañía existente.
     * @param companiaId ID de la compañía a actualizar
     * @param ubicacionId ID de la ubicación a asignar
     * @throws NoSuchElementException Si no se encuentra la compañía o la ubicación
     * @throws IllegalArgumentException Si la ubicación no pasa las validaciones
     */
    public void asignarUbicacion(Integer companiaId, Integer ubicacionId) {
        Compania compania = findByID(companiaId);
        Ubicacion ubicacion = obtenerUbicacion(ubicacionId);
        validarUbicacion(ubicacion);
        compania.setUbicacion(ubicacion);
        companiaRepository.save(compania);
    }

    /**
     * Obtiene una ubicación por su ID.
     * @param ubicacionId ID de la ubicación a buscaSr
     * @return Ubicación encontrada
     * @throws RuntimeException Si no se encuentra la ubicación con el ID especificado
     */
    public Ubicacion obtenerUbicacion(Integer ubicacionId) {
        return ubicacionRepository.findById(ubicacionId)
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada con ID: " + ubicacionId));
    }

}