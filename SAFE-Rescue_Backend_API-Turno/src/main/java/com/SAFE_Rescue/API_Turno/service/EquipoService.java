package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión integral de equipos de emergencia.
 * Maneja operaciones CRUD, asignación de recursos y personal,
 * y validación de datos para equipos de rescate.
 */
@Service
@Transactional
public class EquipoService {

    // REPOSITORIOS INYECTADOS
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private BomberoRepository bomberoRepository;
    @Autowired private CompaniaRepository companiaRepository;
    @Autowired private TipoEquipoRepository tipoEquipoRepository;
    @Autowired private TurnoRepository turnoRepository;
    @Autowired private RecursoRepository recursoRepository;
    @Autowired private VehiculoRepository vehiculoRepository;

    // SERVICIOS INYECTADOS
    @Autowired private TurnoService turnoService;
    @Autowired private CompaniaService companiaService;
    @Autowired private TipoEquipoService tipoEquipoService;


    // MÉTODOS CRUD PRINCIPALES

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

            validarEquipo(equipo);



            return equipoRepository.save(equipo);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el equipo: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza los datos de un equipo existente.
     * @param equipo Datos actualizados del equipo
     * @param id Identificador del equipo a actualizar
     * @return Equipo actualizado
     * @throws IllegalArgumentException Si el equipo proporcionado es nulo
     * @throws NoSuchElementException Si no se encuentra el equipo a actualizar
     * @throws RuntimeException Si ocurre algún error durante la actualización
     */
    public Equipo update(Equipo equipo, long id) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo");
        }

        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + id));

        try {
            actualizarRelaciones(equipo, equipoExistente);
            validarEquipo(equipoExistente);
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
        Equipo equipo = equipoRepository.findById(equipoId)
            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        Compania compania = companiaRepository.findById(companiaId)
            .orElseThrow(() -> new RuntimeException("Compania no encontrada"));
        equipo.setCompania(compania);
        equipoRepository.save(equipo);

    }

    /**
     * Asigna un tipo de equipo a un equipo.
     * @param equipoId ID del equipo
     * @param tipoEquipoId ID del tipo de equipo
     */
    public void asignarTipoEquipo(long equipoId, long tipoEquipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        TipoEquipo tipoEquipo  = tipoEquipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Tipo Equipo no encontrado"));
        equipo.setTipoEquipo(tipoEquipo);
        equipoRepository.save(equipo);
    }

    /**
     * Asigna un turno a un equipo.
     * @param equipoId ID del equipo
     * @param turnoId ID del turno
     */
    public void asignarTurno(long equipoId, long turnoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        Turno turno = turnoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
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

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("No se encontró equipo con ID: " + equipoId));

        equipo.setPersonal(obtenerPersonal(bomberosIds));
        equipoRepository.save(equipo);
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN Y UTILIDADES

    private void validarEquipo(Equipo equipo) {

        if (equipo.getNombre() != null) {
            if (equipo.getNombre().length() > 50) {
                throw new RuntimeException("El nombre no puede exceder 50 caracteres");
            }
        }

        if (equipo.getCantidadMiembros() != null) {
            if (String.valueOf(equipo.getCantidadMiembros()).length()> 5) {
                throw new RuntimeException("El valor de la Cantidad de miembros excede máximo de caracteres (2)");
            }
        }

        if (equipo.getLider() != null) {
            if (equipo.getLider().length() > 50) {
                throw new RuntimeException("El nombre del líder no puede exceder 50 caracteres");
            }
        }

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


    // MÉTODOS PARA CONSULTA

    /**
     * Obtiene información de bomberos desde la base de datos.
     * @param bomberosIds Lista de IDs de bomberos
     * @return Lista con información de bomberos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<Bombero> obtenerPersonal(List<Long> bomberosIds) {
        if (bomberosIds == null || bomberosIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<Bombero> bomberos = new ArrayList<>();
            for (Long id : bomberosIds) {
                Bombero bombero = bomberoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Bombero no encontrado con ID: " + id));
                bomberos.add(bombero);
            }
            return bomberos;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener lista de bomberos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene información de vehículos desde base de datos
     * @param vehiculosIds Lista de IDs de vehículos
     * @return Lista con información de vehículos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<Vehiculo> obtenerVehiculos(List<Long> vehiculosIds) {
        if (vehiculosIds == null || vehiculosIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<Vehiculo> vehiculos = new ArrayList<>();
            for (int i =0;vehiculosIds.size()>i;i++) {
                Vehiculo vehiculo = vehiculoRepository.findById(vehiculosIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado"));
                vehiculos.add(vehiculo);
            }
            return vehiculos;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener lista de vehículos: " + e.getMessage(), e);
        }

    }

    /**
     * Obtiene información de recursos desde base de datos
     * @param recursosIds Lista de IDs de recursos
     * @return Lista con información de recursos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<Recurso> obtenerRecursos(List<Long> recursosIds) {
        if (recursosIds == null || recursosIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<Recurso> recursos = new ArrayList<>();
            for (int i =0;recursosIds.size()>i;i++) {
                Recurso recurso = recursoRepository.findById(recursosIds.get(i))
                        .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
                recursos.add(recurso);
            }
            return recursos;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener lista de Recursos: " + e.getMessage(), e);
        }
    }

}