package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión de equipos de emergencia.
 * Maneja operaciones CRUD, validaciones y comunicación con servicios externos
 * para la gestión integral de equipos de rescate.
 */
@Service
@Transactional
public class EquipoService {

    // CONSTANTES PARA ENDPOINTS EXTERNOS
    private static final String API_BOMBEROS_BASE_URL = "http://api-bomberos/bomberos-resumido";
    private static final String API_VEHICULOS_BASE_URL = "http://api-recursos/vehiculos";
    private static final String API_RECURSOS_BASE_URL = "http://api-recursos/recursos"; // Corregido typo

    // REPOSITORIOS INYECTADOS
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private TurnoService turnoService;
    @Autowired private CompaniaService companiaService;
    @Autowired private TipoEquipoService tipoEquipoService;
    @Autowired private CompaniaRepository companiaRepository;
    @Autowired private TipoEquipoRepository tipoEquipoRepository;
    @Autowired private TurnoRepository turnoRepository;
    @Autowired private RestTemplate restTemplate;

    /**
     * Obtiene todos los equipos registrados en el sistema.
     * @return Lista completa de equipos
     */
    public List<Equipo> findAll() {
        return equipoRepository.findAll();
    }

    /**
     * Busca un equipo por su ID único.
     * @param id Identificador del equipo
     * @return Equipo encontrado
     * @throws NoSuchElementException Si no se encuentra el equipo
     */
    public Equipo findByID(long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró equipo con ID: " + id));
    }

    /**
     * Guarda un nuevo equipo en el sistema.
     * Realiza validaciones y guarda relaciones con otros componentes.
     * @param equipo Datos del equipo a guardar
     * @return Equipo guardado con ID generado
     * @throws RuntimeException Si ocurre algún error durante el proceso
     */
    public Equipo save(Equipo equipo) {
        try {
            // Validación y persistencia de relaciones
            Turno turnoGuardado = turnoService.save(equipo.getTurno());
            Compania companiaGuardada = companiaService.save(equipo.getCompania());
            TipoEquipo tipoEquipoGuardado = tipoEquipoService.save(equipo.getTipoEquipo());

            equipo.setTurno(turnoGuardado);
            equipo.setCompania(companiaGuardada);
            equipo.setTipoEquipo(tipoEquipoGuardado);

            validarEquipo(equipo, equipo);

            // Validación de recursos externos
            if (equipo.getVehiculosAsignados() != null) {
                equipo.setVehiculosAsignados(obtenerVehiculosRemoto(findByIdVehiculo(equipo.getVehiculosAsignados())));
            }

            if (equipo.getPersonal() != null) {
                equipo.setPersonal(obtenerPersonalRemoto(findByIdBombero(equipo.getPersonal())));
            }

            if (equipo.getRecursosDisponibles() != null) {
                equipo.setRecursosDisponibles(obtenerRecursosRemoto(findByIdRecurso((equipo.getRecursosDisponibles()))));
            }

            return equipoRepository.save(equipo);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el equipo: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un equipo existente.
     * @param equipo Datos actualizados del equipo
     * @param id Identificador del equipo a actualizar
     * @return Equipo actualizado
     * @throws IllegalArgumentException Si el equipo es nulo
     * @throws NoSuchElementException Si no se encuentra el equipo
     */
    public Equipo update(Equipo equipo, long id) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo");
        }

        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + id));

        try {
            actualizarRelaciones(equipo, equipoExistente);
            validarEquipo(equipo, equipoExistente);
            return equipoRepository.save(equipoExistente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar equipo: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un equipo del sistema.
     * @param id Identificador del equipo a eliminar
     * @throws NoSuchElementException Si no se encuentra el equipo
     */
    public void delete(long id) {
        if (!equipoRepository.existsById(id)) {
            throw new NoSuchElementException("No se encontró equipo con ID: " + id);
        }
        equipoRepository.deleteById(id);
    }

    // MÉTODOS DE ASIGNACIÓN DE RELACIONES

    /**
     * Asigna una compañía a un equipo.
     * @param equipoId ID del equipo
     * @param companiaId ID de la compañía
     */
    public void asignarCompania(long equipoId, long companiaId) {
        Equipo equipo = buscarEquipo(equipoId);
        Compania compania = buscarCompania(companiaId);
        equipo.setCompania(compania);
        equipoRepository.save(equipo);
    }

    /**
     * Asigna un tipo de equipo a un equipo.
     * @param equipoId ID del equipo
     * @param tipoEquipoId ID del tipo de equipo
     */
    public void asignarTipoEquipo(long equipoId, long tipoEquipoId) {
        Equipo equipo = buscarEquipo(equipoId);
        TipoEquipo tipoEquipo = buscarTipoEquipo(tipoEquipoId);
        equipo.setTipoEquipo(tipoEquipo);
        equipoRepository.save(equipo);
    }

    /**
     * Asigna un turno a un equipo.
     * @param equipoId ID del equipo
     * @param turnoId ID del turno
     */
    public void asignarTurno(long equipoId, long turnoId) {
        Equipo equipo = buscarEquipo(equipoId);
        Turno turno = buscarTurno(turnoId);
        equipo.setTurno(turno);
        equipoRepository.save(equipo);
    }

    /**
     * Asigna una lista de bomberos a un equipo.
     * @param equipoId ID del equipo
     * @param bomberosIds Lista de IDs de bomberos
     * @throws IllegalArgumentException Si la lista es nula o vacía
     */
    public void asignarListaBomberos(long equipoId, List<Long> bomberosIds) {
        if (bomberosIds == null || bomberosIds.isEmpty()) {
            throw new IllegalArgumentException("La lista de bomberos no puede estar vacía");
        }

        Equipo equipo = buscarEquipo(equipoId);
        equipo.setPersonal(obtenerPersonalRemoto(bomberosIds));
        equipoRepository.save(equipo);
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN Y UTILIDADES

    /**
     * Valida los campos de un equipo antes de guardar o actualizar.
     * @param fuente Equipo con datos nuevos
     * @param destino Equipo a actualizar
     * @throws RuntimeException Si hay errores de validación
     */
    private void validarEquipo(Equipo fuente, Equipo destino) {
        if (fuente.getNombre() != null) {
            if (fuente.getNombre().length() > 50) {
                throw new RuntimeException("El nombre no puede exceder 50 caracteres");
            }
            destino.setNombre(fuente.getNombre());
        }

        if (fuente.getCantidadMiembros() != null) {
            if (fuente.getCantidadMiembros() > 99) {
                throw new RuntimeException("La cantidad máxima de miembros es 99");
            }
            destino.setCantidadMiembros(fuente.getCantidadMiembros());
        }

        if (fuente.getLider() != null) {
            if (fuente.getLider().length() > 50) {
                throw new RuntimeException("El nombre del líder no puede exceder 50 caracteres");
            }
            destino.setLider(fuente.getLider());
        }

        destino.setEstado(fuente.isEstado());
    }

    private void actualizarRelaciones(Equipo fuente, Equipo destino) {
        if (fuente.getTurno() != null) {
            destino.setTurno(turnoService.save(fuente.getTurno()));
        }
        if (fuente.getCompania() != null) {
            destino.setCompania(companiaService.save(fuente.getCompania()));
        }
        if (fuente.getTipoEquipo() != null) {
            destino.setTipoEquipo(tipoEquipoService.save(fuente.getTipoEquipo()));
        }
    }

    // MÉTODOS PARA CONSULTA DE SERVICIOS EXTERNOS

    /**
     * Obtiene información de bomberos desde servicio externo.
     * @param bomberosIds Lista de IDs de bomberos
     * @return Lista de DTOs con información de bomberos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<BomberoDTO> obtenerPersonalRemoto(List<Long> bomberosIds) {
        List<BomberoDTO> bomberos = new ArrayList<>();

        try {
            for (Long id : bomberosIds) {
                String url = API_BOMBEROS_BASE_URL + "/" + id;
                ResponseEntity<BomberoDTO> response = restTemplate.getForEntity(url, BomberoDTO.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new RuntimeException("Error al obtener bombero con ID: " + id);
                }

                bomberos.add(response.getBody());
            }
            return bomberos;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar servicio de bomberos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene información de vehículos desde servicio externo.
     * @param vehiculosIds Lista de IDs de vehículos
     * @return Lista de DTOs con información de vehículos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<VehiculoDTO> obtenerVehiculosRemoto(List<Long> vehiculosIds) {
        List<VehiculoDTO> vehiculos = new ArrayList<>();

        try {
            for (Long id : vehiculosIds) {
                String url = API_VEHICULOS_BASE_URL + "/" + id;
                ResponseEntity<VehiculoDTO> response = restTemplate.getForEntity(url, VehiculoDTO.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new RuntimeException("Error al obtener vehículo con ID: " + id);
                }

                vehiculos.add(response.getBody());
            }
            return vehiculos;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar servicio de vehículos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene información de recursos desde servicio externo.
     * @param recursosIds Lista de IDs de recursos
     * @return Lista de DTOs con información de recursos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<RecursoDTO> obtenerRecursosRemoto(List<Long> recursosIds) {
        List<RecursoDTO> recursos = new ArrayList<>();

        try {
            for (Long id : recursosIds) {
                String url = API_RECURSOS_BASE_URL + "/" + id;
                ResponseEntity<RecursoDTO> response = restTemplate.getForEntity(url, RecursoDTO.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new RuntimeException("Error al obtener recurso con ID: " + id);
                }

                recursos.add(response.getBody());
            }
            return recursos;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar servicio de recursos: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae los IDs de una lista de objetos VehiculoDTO.
     *
     * @param vehiculosAsignados Lista de vehículos asignados. Puede ser null o vacía.
     * @return Lista de IDs de vehículos. Retorna una lista vacía si el parámetro es null o vacío,
     *         o si ningún vehículo tiene ID válido.
     */
    private List<Long> findByIdVehiculo(List<VehiculoDTO> vehiculosAsignados) {
        if (vehiculosAsignados == null || vehiculosAsignados.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<>();
        for (VehiculoDTO vehiculo : vehiculosAsignados) {
            if (vehiculo != null && vehiculo.getId() != null) {
                ids.add(vehiculo.getId());
            }
        }
        return ids;
    }

    /**
     * Extrae los IDs de una lista de objetos BomberoDTO.
     *
     * @param bomberosAsignados Lista de bomberos asignados. Puede ser null o vacía.
     * @return Lista de IDs de bomberos. Retorna una lista vacía si el parámetro es null o vacío,
     *         o si ningún bombero tiene ID válido.
     */
    private List<Long> findByIdBombero(List<BomberoDTO> bomberosAsignados) {
        if (bomberosAsignados == null || bomberosAsignados.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<>();
        for (BomberoDTO bombero : bomberosAsignados) {
            if (bombero != null && bombero.getId() != null) {
                ids.add(bombero.getId());
            }
        }
        return ids;
    }

    /**
     * Extrae los IDs de una lista de objetos RecursoDTO.
     *
     * @param recursosAsignados Lista de recursos asignados. Puede ser null o vacía.
     * @return Lista de IDs de recursos. Retorna una lista vacía si el parámetro es null o vacío,
     *         o si ningún recurso tiene ID válido.
     */
    private List<Long> findByIdRecurso(List<RecursoDTO> recursosAsignados) {
        if (recursosAsignados == null || recursosAsignados.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<>();
        for (RecursoDTO recurso : recursosAsignados) {
            if (recurso != null && recurso.getId() != null) {
                ids.add(recurso.getId());
            }
        }
        return ids;
    }

    // MÉTODOS AUXILIARES PRIVADOS

    /**
     * Busca un equipo por su ID en el repositorio.
     *
     * @param equipoId ID del equipo a buscar (debe ser positivo)
     * @return Equipo encontrado
     * @throws RuntimeException Si no se encuentra el equipo con el ID especificado
     * @throws IllegalArgumentException Si el ID proporcionado no es válido
     */
    private Equipo buscarEquipo(long equipoId) {
        if (equipoId <= 0) {
            throw new IllegalArgumentException("El ID del equipo debe ser un número positivo");
        }
        return equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("No se encontró equipo con ID: " + equipoId));
    }

    /**
     * Busca una Compania por su ID en el repositorio.
     *
     * @param companiaId ID de la compania a buscar (debe ser positivo)
     * @return Compania encontrado
     * @throws RuntimeException Si no se encuentra la Compania con el ID especificado
     * @throws IllegalArgumentException Si el ID proporcionado no es válido
     */
    private Compania buscarCompania(long companiaId) {
        if (companiaId <= 0) {
            throw new IllegalArgumentException("El ID de la compania debe ser un número positivo");
        }
        return companiaRepository.findById(companiaId)
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));
    }

    /**
     * Busca un Tipo de Equipo por su ID en el repositorio.
     *
     * @param tipoEquipoId ID del Tipo de Equipo a buscar (debe ser positivo)
     * @return Tipo de Equipo encontrado
     * @throws RuntimeException Si no se encuentra el Tipo de Equipo con el ID especificado
     * @throws IllegalArgumentException Si el ID proporcionado no es válido
     */
    private TipoEquipo buscarTipoEquipo(long tipoEquipoId) {
        if (tipoEquipoId <= 0) {
            throw new IllegalArgumentException("El ID del Tipo de Equipo debe ser un número positivo");
        }
        return tipoEquipoRepository.findById(tipoEquipoId)
                .orElseThrow(() -> new RuntimeException("Tipo de equipo no encontrado"));
    }

    /**
     * Busca un Turno por su ID en el repositorio.
     *
     * @param turnoId ID del Turno a buscar (debe ser positivo)
     * @return Turno encontrado
     * @throws RuntimeException Si no se encuentra el Turno con el ID especificado
     * @throws IllegalArgumentException Si el ID proporcionado no es válido
     */
    private Turno buscarTurno(long turnoId) {
        if (turnoId <= 0) {
            throw new IllegalArgumentException("El ID del Turno debe ser un número positivo");
        }
        return turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }
}