package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.service.TurnoService;
import com.SAFE_Rescue.API_Turno.modelo.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST para gestionar operaciones relacionadas con turnos.
 * Proporciona endpoints para listar, crear, buscar, actualizar y eliminar turnos.
 */
@RestController
@RequestMapping("/api-turnos/v1/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene la lista de todos los turnos existentes.
     *
     * @return ResponseEntity con la lista de turnos si existen,
     *         o código de estado NO_CONTENT (204) si la lista está vacía.
     */
    @GetMapping
    @Operation(summary = "Obtener todos los turnos", description = "Devuelve una lista de todos los turnos existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turnos encontrada"),
            @ApiResponse(responseCode = "204", description = "No hay turnos registrados")
    })
    public ResponseEntity<List<Turno>> listar() {
        List<Turno> turnos = turnoService.findAll();
        if (turnos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(turnos);
    }

    /**
     * Crea un nuevo turno.
     *
     * @param turno El objeto Turno a crear, recibido en el cuerpo de la petición.
     * @return ResponseEntity con mensaje de éxito y código CREATED (201) si se crea correctamente,
     *         BAD_REQUEST (400) si hay errores de validación,
     *         o INTERNAL_SERVER_ERROR (500) si ocurre un error inesperado.
     */
    @PostMapping
    @Operation(summary = "Crear nuevo turno", description = "Crea un nuevo turno en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turno creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> agregarTurno(@RequestBody Turno turno) {
        try {
            Turno nuevoTurno = turnoService.save(turno);
            return ResponseEntity.status(HttpStatus.CREATED).body("Turno creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Busca un turno por su ID.
     *
     * @param id El ID del turno a buscar.
     * @return ResponseEntity con el turno encontrado si existe,
     *         o NOT_FOUND (404) si no se encuentra el turno.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar turno por ID", description = "Devuelve un turno específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turno encontrado"),
            @ApiResponse(responseCode = "404", description = "Turno no encontrado")
    })
    public ResponseEntity<?> buscarTurno(@PathVariable Integer id) {
        Turno turno;
        try {
            turno = turnoService.findByID(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("Turno no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(turno);
    }

    /**
     * Actualiza un turno existente.
     *
     * @param id El ID del turno a actualizar.
     * @param turno El objeto Turno con los nuevos datos.
     * @return ResponseEntity con mensaje de éxito si se actualiza correctamente,
     *         NOT_FOUND (404) si no se encuentra el turno,
     *         BAD_REQUEST (400) si hay errores de validación,
     *         o INTERNAL_SERVER_ERROR (500) si ocurre un error inesperado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar turno", description = "Actualiza la información de un turno existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turno actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Turno no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> actualizarTurno1(@PathVariable Integer id, @RequestBody Turno turno) {
        try {
            Turno nuevoTurno = turnoService.update(turno, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turno no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un turno existente.
     *
     * @param id El ID del turno a eliminar.
     * @return ResponseEntity con mensaje de éxito si se elimina correctamente,
     *         NOT_FOUND (404) si no se encuentra el turno,
     *         BAD_REQUEST (400) si hay errores en la operación,
     *         o INTERNAL_SERVER_ERROR (500) si ocurre un error inesperado.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar turno", description = "Elimina un turno del sistema dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turno eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Turno no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error en la operación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> eliminarTurno(@PathVariable Integer id) {
        try {
            turnoService.delete(id);
            return ResponseEntity.ok("Turno eliminado con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turno no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}