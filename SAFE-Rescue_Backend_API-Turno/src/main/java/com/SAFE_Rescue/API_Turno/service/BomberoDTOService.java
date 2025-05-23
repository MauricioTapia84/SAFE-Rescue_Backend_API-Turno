package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.BomberoDTO;
import com.SAFE_Rescue.API_Turno.repository.BomberoDTORepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar operaciones relacionadas con bomberos, incluyendo:
 * - Obtención de datos desde API externa
 * - Almacenamiento local de datos
 * - Integración entre fuentes externas y locales
 */
@Service
public class BomberoDTOService {

    private static final String API_EXTERNA_URL = "http://localhost:8080/api-administrador/v1/bomberos";

    private final RestTemplate restTemplate;
    private final BomberoDTORepository bomberoDTORepository;

    @Autowired
    public BomberoDTOService(RestTemplate restTemplate, BomberoDTORepository bomberoDTORepository) {
        this.restTemplate = restTemplate;
        this.bomberoDTORepository = bomberoDTORepository;
    }

    /**
     * Obtiene la lista completa de bomberos desde la API externa.
     *
     * @return Lista de {@link BomberoDTO} obtenida desde la API.
     *         Retorna lista vacía si no hay datos o falla la conexión.
     * @throws RestClientException Si ocurre un error al comunicarse con la API externa.
     */
    public List<BomberoDTO> obtenerBomberosDesdeAPI() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    API_EXTERNA_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return Optional.ofNullable(response.getBody())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(item -> {
                        BomberoDTO dto = new BomberoDTO();
                        dto.setId(((Number) item.get("id")).longValue());
                        dto.setNombre((String) item.get("nombre"));
                        dto.setA_paterno((String) item.get("a_paterno"));
                        dto.setA_materno((String) item.get("a_materno"));
                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener bomberos", e);
        }
    }


    /**
     * Obtiene todos los bomberos almacenados localmente.
     *
     * @return Lista de {@link BomberoDTO} almacenados localmente.
     */
    public List<BomberoDTO> obtenerTodosLosBomberos() {
        return bomberoDTORepository.obtenerBomberos();
    }

    /**
     * Agrega un nuevo bombero al repositorio local.
     *
     * @param bombero El {@link BomberoDTO} a agregar.
     * @throws IllegalArgumentException Si el bombero es nulo.
     */
    public void agregarBombero(BomberoDTO bombero) {
        if (bombero == null) {
            throw new IllegalArgumentException("El objeto BomberoDTO no puede ser nulo");
        }
        bomberoDTORepository.agregarBombero(bombero);
    }

    /**
     * Carga bomberos desde la API externa y los almacena en el repositorio local.
     *
     * @return Lista de {@link BomberoDTO} cargados desde la API externa.
     * @throws RestClientException Si ocurre un error al comunicarse con la API externa.
     */
    public List<BomberoDTO> cargarYAlmacenarBomberosDesdeAPI() {
        List<BomberoDTO> bomberosExternos = obtenerBomberosDesdeAPI();

        if (!bomberosExternos.isEmpty()) {
            bomberosExternos.forEach(bombero -> {
                if (bombero != null) {
                    bomberoDTORepository.agregarBombero(bombero);
                }
            });
        }

        return bomberosExternos;
    }

    /**
     * Obtiene un BomberoDTO por su ID desde el repositorio local o API externa
     * @param id ID del bombero a buscar
     * @return BomberoDTO encontrado, o null si no existe
     */
    public BomberoDTO obtenerBomberoPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        List<BomberoDTO> bomberos = bomberoDTORepository.obtenerBomberos(); // Obtiene todos los bomberos

        for (BomberoDTO bombero : bomberos) {
            if (bombero.getId().equals(id)) {
                return bombero; // Retorna al primer match
            }
        }

        return null; // Si no se encuentra
    }



}