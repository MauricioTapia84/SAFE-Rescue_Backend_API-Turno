package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.modelo.Ubicacion;
import com.SAFE_Rescue.API_Turno.repository.CompaniaRepository;
import com.SAFE_Rescue.API_Turno.repository.UbicacionRepository;
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

    @Autowired
    private CompaniaRepository companiaRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

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
    public Compania findByID(long id) {
        return companiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compañía no encontrada con ID: " + id));
    }

    /**
     * Guarda una nueva compañía con validación de ubicación remota.
     * @param compania Compañía a guardar
     * @return Compañía guardada
     * @throws IllegalArgumentException Si la compañía no pasa las validaciones
     * @throws RuntimeException Si no se puede verificar la ubicación remota
     */
    public Compania save(Compania compania) {

        Ubicacion ubicacionGuargada=compania.getUbicacion();

        validarUbicacion(ubicacionGuargada);

        compania.setUbicacion(ubicacionGuargada);

        validarCompania(compania);

        return companiaRepository.save(compania);
    }

    /**
     * Actualiza una compañía existente con validación de ubicación remota.
     * @param compania Datos actualizados de la compañía
     * @param id ID de la compañía a actualizar
     * @return Compañía actualizada
     * @throws NoSuchElementException Si no se encuentra la compañía
     * @throws IllegalArgumentException Si los datos no pasan las validaciones
     */
    public Compania update(Compania compania, long id) {
        Compania antiguaCompania = companiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compañía no encontrada con ID: " + id));

        validarCompania(compania);
        if (compania.getNombre() != null) {
            antiguaCompania.setNombre(compania.getNombre());
        }

        if (compania.getUbicacion() != null) {
            compania.getUbicacion();
            antiguaCompania.setUbicacionExternaId(compania.getUbicacionExternaId());
        }

        return companiaRepository.save(antiguaCompania);
    }

    /**
     * Elimina una compañía por su ID.
     * @param id ID de la compañía a eliminar
     * @throws NoSuchElementException Si no se encuentra la compañía
     */
    public void delete(long id) {
        if (!companiaRepository.existsById(id)) {
            throw new NoSuchElementException("Compañía no encontrada con ID: " + id);
        }
        companiaRepository.deleteById(id);
    }


    public Ubicacion obtenerUbicacion(Long ubicacionId) {
        return ubicacionRepository.findById(ubicacionId)
                .orElseThrow(() -> new RuntimeException("Ubicación no encontrada con ID: " + ubicacionId));
    }

    /**
     * Valida los datos básicos de una compañía.
     * @param compania Compañía a validar
     * @throws IllegalArgumentException Si la compañía no cumple con las reglas de validación
     */
    private void validarCompania(Compania compania) {
        if (compania.getNombre() == null || compania.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la compañía es requerido");
        }
        if (compania.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder los 50 caracteres");
        }
    }


    private void validarUbicacion(Ubicacion ubicacion) {

        if (0 <= ubicacion.getNumeracion() ) {
            throw new IllegalArgumentException("La numeración de la calle es requerido");
        } else {
            if (String.valueOf(ubicacion.getNumeracion()).length()> 5) {
                throw new RuntimeException("El valor de la Numeración excede máximo de caracteres (5)");
            }
        }

        if (ubicacion.getCalle() != null) {
            if (ubicacion.getCalle().length() > 50) {
                throw new RuntimeException("El nombre de la calle no puede exceder 50 caracteres");
            }
        }else{
            throw new IllegalArgumentException("El nombre de la calle es requerido");
        }

        if (ubicacion.getComuna() != null) {
            if (ubicacion.getComuna().length() > 50) {
                throw new RuntimeException("El nombre de la comuna no puede exceder 50 caracteres");
            }
        }else{
            throw new IllegalArgumentException("El nombre de la comuna es requerido");
        }

        if (ubicacion.getRegion() != null) {
            if (ubicacion.getRegion().length() > 50) {
                throw new RuntimeException("El nombre de la Región no puede exceder 50 caracteres");
            }
        }else{
            throw new IllegalArgumentException("El nombre de la Región es requerido");
        }

    }


    // MÉTODOS DE ASIGNACIÓN DE RELACIONES

    /**
     * Asigna una ubicación a una compañía.
     * @param companiaId ID de la compañía
     * @param ubicacionId ID de la ubicacion
     */
    public void asignarUbicacion(Long companiaId, Long ubicacionId) {
        Compania compania = findByID(companiaId);
        Ubicacion ubicacion = obtenerUbicacion(ubicacionId);
        compania.setUbicacion(ubicacion);
        companiaRepository.save(compania);
    }

}