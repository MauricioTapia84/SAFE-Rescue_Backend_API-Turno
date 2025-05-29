package com.SAFE_Rescue.API_Incidentes.service;

import com.SAFE_Rescue.API_Incidentes.modelo.Equipo;
import com.SAFE_Rescue.API_Incidentes.repository.EquipoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para gestionar operaciones relacionadas con los turnos.
 * Proporciona métodos para CRUD y validaciones de turnos.
 */
@Service
@Transactional
public class EstadoIncidenteService {

    @Autowired
    private EquipoRepository turnoRepository;

    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todos los turnos existentes.
     * @return Lista de todos los turnos
     */
    public List<Equipo> findAll() {
        return turnoRepository.findAll();
    }

    /**
     * Busca un turno por su ID.
     * @param id ID del turno a buscar
     * @return El turno encontrado
     * @throws NoSuchElementException Si no se encuentra el turno
     */
    public Equipo findByID(long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo con ID " + id + " no encontrado"));
    }

    /**
     * Guarda un nuevo equipo después de validarlo.
     * @param equipo Equipo a guardar
     * @return Equipo guardado
     * @throws RuntimeException Si hay errores de validación o al guardar
     */
    public Equipo save(Equipo equipo) {
        try {
            validarTurno(equipo);
            return turnoRepository.save(equipo);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar el Equipo: " + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado: " + ex.getMessage());
        }
    }

    /**
     * Actualiza un equipo existente.
     * @param equipo Equipo con los nuevos datos
     * @param id ID del equipo a actualizar
     * @return Equipo actualizado
     * @throws RuntimeException Si hay errores de validación o al actualizar
     */
    public Equipo update(Equipo equipo, long id) {
        try {
            Equipo antiguoEquipo = turnoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado"));

            // Actualización de campos con validación
            if (equipo.getNombre() != null) {
                if (equipo.getNombre().length() > 50) {
                    throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
                }
                antiguoEquipo.setNombre(equipo.getNombre());
            }

            if (equipo.getDuracion() != null) {
                if (String.valueOf(equipo.getDuracion()).length() > 2) {
                    throw new RuntimeException("El valor de la duración excede máximo de caracteres (2)");
                }
                antiguoEquipo.setDuracion(equipo.getDuracion());
            }

            if (equipo.getFechaHoraInicio() != null) {
                antiguoEquipo.setFechaHoraInicio(equipo.getFechaHoraInicio());
            }

            if (equipo.getFechaHoraFin() != null) {
                antiguoEquipo.setFechaHoraFin(equipo.getFechaHoraFin());
            }

            // Validar consistencia de fechas después de la actualización
            validarFechas(antiguoEquipo.getFechaHoraInicio(), antiguoEquipo.getFechaHoraFin());

            // Calcular duracion de equipo
            antiguoEquipo.setDuracion(calcularDuracion(antiguoEquipo));

            return turnoRepository.save(antiguoEquipo);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Equipo: " + e.getMessage());
        }
    }

    /**
     * Elimina un turno por su ID.
     * @param id ID del turno a eliminar
     * @throws RuntimeException Si no se encuentra el turno o hay error al eliminar
     */
    public void delete(long id) {
        try {
            if (!turnoRepository.existsById(id)) {
                throw new NoSuchElementException("Equipo no encontrado");
            }
            turnoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar Equipo: " + e.getMessage());
        }
    }

    /**
     * Valida los datos de un equipo antes de guardarlo o actualizarlo.
     * @param equipo Equipo a validar
     * @throws RuntimeException Si alguna validación falla
     */
    public void validarTurno(@NotNull Equipo equipo) {
        if (equipo.getNombre() == null || equipo.getNombre().isEmpty()) {
            throw new RuntimeException("El nombre del equipo es requerido");
        }

        if (equipo.getNombre().length() > 50) {
            throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
        }

        if (equipo.getDuracion() != null && String.valueOf(equipo.getDuracion()).length() > 2) {
            throw new RuntimeException("El valor de la duración excede máximo de caracteres (2)");
        }

        if (equipo.getFechaHoraInicio() == null) {
            throw new RuntimeException("La fecha/hora de inicio es requerida");
        }

        if (equipo.getFechaHoraFin() == null) {
            throw new RuntimeException("La fecha/hora de fin es requerida");
        }

        validarFechas(equipo.getFechaHoraInicio(), equipo.getFechaHoraFin());

        equipo.setDuracion(calcularDuracion(equipo));
    }

    /**
     * Calcula la duración automática del equipo en horas.
     * @param equipo Equipo para calcular la duración
     * @return Duración en horas entre fechaHoraInicio y fechaHoraFin
     */
    private long calcularDuracion(Equipo equipo) {
        if (equipo.getFechaHoraInicio() == null || equipo.getFechaHoraFin() == null) {
            throw new RuntimeException("Las fechas de inicio y fin son requeridas para calcular la duración");
        }
        return java.time.Duration.between(equipo.getFechaHoraInicio(), equipo.getFechaHoraFin()).toHours();
    }

    /**
     * Valida la consistencia de las fechas del turno.
     * @param fechaHoraInicio Fecha de inicio del turno
     * @param fechaHoraFin Fecha de fin del turno
     * @throws IllegalArgumentException Si las fechas son inconsistentes
     */
    private void validarFechas(java.time.LocalDateTime fechaHoraInicio, java.time.LocalDateTime fechaHoraFin) {
        if (fechaHoraInicio == null || fechaHoraFin == null) {
            throw new IllegalArgumentException("Ambas fechas son requeridas para la validación");
        }
        if (fechaHoraInicio.isAfter(fechaHoraFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }
}