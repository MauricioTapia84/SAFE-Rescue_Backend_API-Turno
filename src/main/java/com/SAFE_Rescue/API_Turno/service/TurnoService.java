package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.repository.TurnoRepository;
import com.SAFE_Rescue.API_Turno.modelo.Turno;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todos los turnos existentes.
     * @return Lista de todos los turnos
     */
    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    /**
     * Busca un turno por su ID.
     * @param id ID del turno a buscar
     * @return El turno encontrado
     * @throws NoSuchElementException Si no se encuentra el turno
     */
    public Turno findByID(Integer id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Turno con ID " + id + " no encontrado"));
    }

    /**
     * Guarda un nuevo turno después de validarlo.
     * @param turno Turno a guardar
     * @return Turno guardado
     * @throws RuntimeException Si hay errores de validación o al guardar
     */
    public Turno save(Turno turno) {
        try {
            validarTurno(turno);
            return turnoRepository.save(turno);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar el Turno: " + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado: " + ex.getMessage());
        }
    }

    /**
     * Actualiza un turno existente.
     * @param turno Turno con los nuevos datos
     * @param id ID del turno a actualizar
     * @return Turno actualizado
     * @throws RuntimeException Si hay errores de validación o al actualizar
     */
    public Turno update(Turno turno, Integer id) {
        try {
            Turno antiguoTurno = turnoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Turno no encontrado"));

            // Actualización de campos con validación
            if (turno.getNombre() != null) {
                if (turno.getNombre().length() > 50) {
                    throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
                }
                antiguoTurno.setNombre(turno.getNombre());
            }

            if (turno.getDuracion() != null) {
                if (String.valueOf(turno.getDuracion()).length() > 2) {
                    throw new RuntimeException("El valor de la duración excede máximo de caracteres (2)");
                }
                antiguoTurno.setDuracion(turno.getDuracion());
            }

            if (turno.getFechaHoraInicio() != null) {
                antiguoTurno.setFechaHoraInicio(turno.getFechaHoraInicio());
            }

            if (turno.getFechaHoraFin() != null) {
                antiguoTurno.setFechaHoraFin(turno.getFechaHoraFin());
            }

            // Validar consistencia de fechas después de la actualización
            validarFechas(antiguoTurno.getFechaHoraInicio(), antiguoTurno.getFechaHoraFin());

            // Calcular duracion de turno
            antiguoTurno.setDuracion(calcularDuracion(antiguoTurno));

            return turnoRepository.save(antiguoTurno);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Turno: " + e.getMessage());
        }
    }

    /**
     * Elimina un turno por su ID.
     * @param id ID del turno a eliminar
     * @throws RuntimeException Si no se encuentra el turno o hay error al eliminar
     */
    public void delete(Integer id) {
        try {
            if (!turnoRepository.existsById(id)) {
                throw new NoSuchElementException("Turno no encontrado");
            }
            turnoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar Turno: " + e.getMessage());
        }
    }

    /**
     * Valida los datos de un turno antes de guardarlo o actualizarlo.
     * @param turno Turno a validar
     * @throws RuntimeException Si alguna validación falla
     */
    public void validarTurno(Turno turno) {
        if (turno.getNombre() == null || turno.getNombre().isEmpty()) {
            throw new RuntimeException("El nombre del turno es requerido");
        }

        if (turno.getNombre().length() > 50) {
            throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
        }

        if (turno.getDuracion() != null && String.valueOf(turno.getDuracion()).length() > 2) {
            throw new RuntimeException("El valor de la duración excede máximo de caracteres (2)");
        }

        if (turno.getFechaHoraInicio() == null) {
            throw new RuntimeException("La fecha/hora de inicio es requerida");
        }

        if (turno.getFechaHoraFin() == null) {
            throw new RuntimeException("La fecha/hora de fin es requerida");
        }

        validarFechas(turno.getFechaHoraInicio(), turno.getFechaHoraFin());

        turno.setDuracion(calcularDuracion(turno));
    }

    /**
     * Calcula la duración automática del turno en horas.
     * @param turno Turno para calcular la duración
     * @return Duración en horas entre fechaHoraInicio y fechaHoraFin
     */
    private long calcularDuracion(Turno turno) {
        if (turno.getFechaHoraInicio() == null || turno.getFechaHoraFin() == null) {
            throw new RuntimeException("Las fechas de inicio y fin son requeridas para calcular la duración");
        }
        return java.time.Duration.between(turno.getFechaHoraInicio(), turno.getFechaHoraFin()).toHours();
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