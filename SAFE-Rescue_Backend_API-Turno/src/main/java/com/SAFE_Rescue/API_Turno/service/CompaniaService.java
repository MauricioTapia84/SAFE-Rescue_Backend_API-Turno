package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.modelo.UbicacionDTO;
import com.SAFE_Rescue.API_Turno.repository.CompaniaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

    private static final String API_UBICACIONES_BASE_URL = "http://api-incidentes/ubicaciones";

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
        validarCompania(compania);

        // Validar ubicación remota
        if (compania.getUbicacionExternaId() != null) {
            validarUbicacionExterna(compania.getUbicacionExternaId());
        }

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

        if (compania.getNombre() != null) {
            validarNombreCompania(compania.getNombre());
            antiguaCompania.setNombre(compania.getNombre());
        }

        if (compania.getUbicacionExternaId() != null) {
            validarUbicacionExterna(compania.getUbicacionExternaId());
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


    /**
     * Obtiene información completa de una ubicación remota.
     * @param ubicacionId ID de la ubicación a consultar
     * @return DTO con la información completa de la ubicación
     * @throws RuntimeException Si la ubicación no existe o hay error en la comunicación
     */
    public UbicacionDTO obtenerUbicacionRemota(Long ubicacionId) {
        String url = API_UBICACIONES_BASE_URL + "/" + ubicacionId;
        try {
            ResponseEntity<UbicacionDTO> response = restTemplate.getForEntity(
                    url,
                    UbicacionDTO.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Ubicación no encontrada con ID: " + ubicacionId);
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar ubicación remota: " + e.getMessage(), e);
        }
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
        validarNombreCompania(compania.getNombre());
    }

    /**
     * Valida el nombre de una compañía.
     * @param nombre Nombre a validar
     * @throws IllegalArgumentException Si el nombre excede el límite de caracteres
     */
    private void validarNombreCompania(String nombre) {
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder los 50 caracteres");
        }
    }

    /**
     * Valida que una ubicación exista en el servicio externo
     */
    private void validarUbicacionExterna(Long ubicacionId) {
        if (ubicacionId != null) {
            obtenerUbicacionRemota(ubicacionId); // Si falla, lanzará excepción
        }
    }

    // MÉTODOS DE ASIGNACIÓN DE RELACIONES

    /**
     * Asigna una ubicación a una compañía.
     * @param companiaId ID de la compañía
     * @param ubicacionId ID de la ubicacion
     */
    public void asignarUbicacion(long companiaId, long ubicacionId) {
        Compania compania = findByID(companiaId);
        validarUbicacionExterna(ubicacionId); // Valida que exista
        compania.setUbicacionExternaId(ubicacionId);
        companiaRepository.save(compania);
    }

}