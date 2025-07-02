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
    public Equipo findByID(Integer id) {
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
            // Validación y persistencia de relaciones principales
            Turno turnoGuardado = turnoService.save(equipo.getTurno());
            Compania companiaGuardada = companiaService.save(equipo.getCompania());
            TipoEquipo tipoEquipoGuardado = tipoEquipoService.save(equipo.getTipoEquipo());

            equipo.setTurno(turnoGuardado);
            equipo.setCompania(companiaGuardada);
            equipo.setTipoEquipo(tipoEquipoGuardado);

            // Asignación de recursos asociados
            asignarVehiculosAlEquipo(equipo);
            asignarBomberosAlEquipo(equipo);
            asignarRecursosAlEquipo(equipo);

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
    public Equipo update(Equipo equipo, Integer id) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo");
        }

        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + id));

        try {
            actualizarRelaciones(equipo, equipoExistente);

            // Actualizar recursos asociados
            if (equipo.getVehiculos() != null) {
                asignarVehiculosAlEquipo(equipo);
                equipoExistente.setVehiculos(equipo.getVehiculos());
            }

            if (equipo.getPersonal() != null) {
                asignarBomberosAlEquipo(equipo);
                equipoExistente.setPersonal(equipo.getPersonal());
            }

            if (equipo.getRecursos() != null) {
                asignarRecursosAlEquipo(equipo);
                equipoExistente.setRecursos(equipo.getRecursos());
            }

            validarEquipo(equipoExistente);
            return equipoRepository.save(equipoExistente);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error al actualizar equipo: " + e.getMessage());
        } catch (NoSuchElementException  f) {
            throw new NoSuchElementException("Error al actualizar equipo: " + f.getMessage());
        } catch (Exception g) {
            throw new RuntimeException("Error al actualizar equipo: " + g.getMessage());
        }
    }

    /**
     * Elimina un equipo del sistema.
     * @param id Identificador del equipo a eliminar
     * @throws NoSuchElementException Si no se encuentra el equipo
     */
    public void delete(Integer id) {
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
    public void asignarCompania(Integer equipoId, Integer companiaId) {
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
    public void asignarTipoEquipo(Integer equipoId, Integer tipoEquipoId) {
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
    public void asignarTurno(Integer equipoId, Integer turnoId) {
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
    public void asignarListaBomberos(Integer equipoId, List<Integer> bomberosIds) {
        if (bomberosIds == null || bomberosIds.isEmpty()) {
            throw new IllegalArgumentException("La lista de bomberos no puede estar vacía");
        }

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("No se encontró equipo con ID: " + equipoId));

        equipo.setPersonal(obtenerPersonal(bomberosIds));
        equipoRepository.save(equipo);
    }
    /**
     * Asigna bomberos a un equipo obteniéndolos de la base de datos
     * @param equipo Equipo al que se asignarán los bomberos
     */
    private void asignarBomberosAlEquipo(Equipo equipo) {
        if (equipo.getPersonal() != null && !equipo.getPersonal().isEmpty()) {
            List<Integer> bomberosIds = extraerIdsDeBomberos(equipo.getPersonal());
            equipo.setPersonal(obtenerPersonal(bomberosIds));
        }
    }

    /**
     * Asigna recursos a un equipo obteniéndolos de la base de datos
     * @param equipo Equipo al que se asignarán los recursos
     */
    private void asignarRecursosAlEquipo(Equipo equipo) {
        if (equipo.getRecursos() != null && !equipo.getRecursos().isEmpty()) {
            List<Integer> recursosIds = extraerIdsRecursos(equipo.getRecursos());
            equipo.setRecursos(obtenerRecursos(recursosIds));
        }
    }

    /**
     * Asigna vehículos a un equipo obteniéndolos de la base de datos.
     *
     * @param equipo Equipo al que se asignarán los vehículos.
     *               Debe ser no nulo y debe tener una lista de vehículos.
     */
    private void asignarVehiculosAlEquipo(Equipo equipo) {
        if (equipo != null && equipo.getVehiculos() != null && !equipo.getVehiculos().isEmpty()) {
            List<Integer> vehiculosIds = extraerIdsVehiculos(equipo.getVehiculos());
            equipo.setVehiculos(obtenerVehiculos(vehiculosIds));
        }
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN Y UTILIDADES

    /**
     * Valida los campos básicos de un objeto Equipo antes de su persistencia.
     * <p>
     * Realiza las siguientes validaciones:
     * <ul>
     *   <li>Longitud máxima del nombre (50 caracteres)</li>
     *   <li>Formato y longitud de la cantidad de miembros (máximo 5 dígitos)</li>
     *   <li>Longitud máxima del nombre del líder (50 caracteres)</li>
     * </ul>
     *
     * @param equipo Objeto Equipo a validar
     * @throws IllegalArgumentException Si alguna validación falla, con mensaje descriptivo del error
     * @throws NullPointerException Si el parámetro equipo es nulo
     */
    public void validarEquipo(Equipo equipo) {
        try{
            if (equipo.getNombre() != null) {
                if (equipo.getNombre().length() > 50) {
                    throw new IllegalArgumentException("El nombre no puede exceder 50 caracteres");
                }
            }else{
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }

            if (equipo.getCantidadMiembros() != null) {
                if (equipo.getCantidadMiembros()< 0) {
                    throw new IllegalArgumentException("El valor de la Cantidad de miembros no puede ser negativa");
                }

                if (String.valueOf(equipo.getCantidadMiembros()).length()> 5) {
                    throw new IllegalArgumentException("El valor de la Cantidad de miembros excede máximo de caracteres (2)");
                }

            }

            if (equipo.getLider() != null) {
                if (equipo.getLider().length() > 50) {
                    throw new IllegalArgumentException("El nombre del líder no puede exceder 50 caracteres");
                }
            }else{
                throw new IllegalArgumentException("El nombre del líder no puede ser nulo");
            }

        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error al validar equipo: " + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error al validar equipo: " + ex.getMessage());
        }

    }

    /**
     * Actualiza las relaciones de un equipo existente con los valores de un equipo fuente.
     * <p>
     * Actualiza las siguientes relaciones si están presentes en el equipo fuente:
     * <ul>
     *   <li>Turno</li>
     *   <li>Compañía</li>
     *   <li>Tipo de equipo</li>
     * </ul>
     * Cada relación se persiste mediante su respectivo servicio antes de asignarse.
     *
     * @param fuente Equipo con los nuevos valores de las relaciones
     * @param destino Equipo existente que será actualizado
     * @throws IllegalStateException Si ocurre un error al persistir alguna relación
     * @throws NullPointerException Si alguno de los parámetros es nulo
     */
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
    private List<Bombero> obtenerPersonal(List<Integer> bomberosIds) {
        if (bomberosIds == null || bomberosIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<Bombero> bomberos = new ArrayList<>();
            for (Integer id : bomberosIds) {
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
     * Obtiene información de vehículos desde la base de datos.
     *
     * @param vehiculosIds Lista de IDs de vehículos.
     * @return Lista con información de vehículos.
     * @throws NoSuchElementException Si algún vehículo no se encuentra.
     * @throws RuntimeException Si hay un error en la comunicación.
     */
    private List<Vehiculo> obtenerVehiculos(List<Integer> vehiculosIds) {
        if (vehiculosIds == null || vehiculosIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Vehiculo> vehiculos = new ArrayList<>();
        for (Integer id : vehiculosIds) {
            Vehiculo vehiculo = vehiculoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado con ID: " + id));
            vehiculos.add(vehiculo);
        }
        return vehiculos;
    }

    /**
     * Obtiene información de recursos desde base de datos
     * @param recursosIds Lista de IDs de recursos
     * @return Lista con información de recursos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<Recurso> obtenerRecursos(List<Integer> recursosIds) {
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

    /**
     * Obtiene IDs de bomberos desde base de datos
     * @param bomberos Lista de bomberos
     * @return Lista con información de bomberos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<Integer> extraerIdsDeBomberos(List<Bombero> bomberos) {
        if (bomberos == null || bomberos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> ids = new ArrayList<>();
        for (Bombero bombero : bomberos) {
            ids.add(bombero.getId());
        }
        return ids;
    }

    /**
     * Obtiene los IDs de los vehículos desde la lista proporcionada.
     *
     * @param vehiculos Lista de vehículos.
     * @return Lista con los IDs de los vehículos.
     * @throws RuntimeException Si hay un error en el proceso de extracción.
     */
    private List<Integer> extraerIdsVehiculos(List<Vehiculo> vehiculos) {
        if (vehiculos == null || vehiculos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> ids = new ArrayList<>();
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo != null) { // Verificar que el vehículo no sea nulo
                ids.add(vehiculo.getId()); // Asumiendo que getId() retorna Integer
            }
        }
        return ids;
    }

    /**
     * Obtiene IDs de recursos desde base de datos
     * @param recursos Lista recursos
     * @return Lista con información de recursos
     * @throws RuntimeException Si hay error en la comunicación
     */
    private List<Integer> extraerIdsRecursos(List<Recurso> recursos) {
        if (recursos == null || recursos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> ids = new ArrayList<>();
        for (Recurso recurso: recursos) {
            ids.add(recurso.getId());
        }
        return ids;
    }

}