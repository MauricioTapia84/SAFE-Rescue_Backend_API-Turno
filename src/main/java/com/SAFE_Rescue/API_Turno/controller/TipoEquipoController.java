package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Turno.service.TipoEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de tipos de equipo de emergencia.
 * Proporciona endpoints para operaciones CRUD de tipos de equipo.
 */
@RestController
@RequestMapping("/api-turnos/v1/tipos-equipo")
public class TipoEquipoController {

    // SERVICIOS INYECTADOS
    @Autowired private TipoEquipoService tipoEquipoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los tipos de equipo registrados en el sistema.
     * @return ResponseEntity con lista de tipos de equipo o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    public ResponseEntity<List<TipoEquipo>> listarTiposEquipo() {
        List<TipoEquipo> tipoEquipo = tipoEquipoService.findAll();
        if(tipoEquipo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(tipoEquipo);
    }

    /**
     * Busca un tipo de equipo por su ID.
     * @param id ID del tipo de equipo a buscar
     * @return ResponseEntity con el tipo de equipo encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTipoEquipo(@PathVariable int id) {
        TipoEquipo tipoEquipo;
        try {
            tipoEquipo = tipoEquipoService.findByID(id);
        } catch(NoSuchElementException e) {
            return new ResponseEntity<String>("Tipo Equipo no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(tipoEquipo);
    }

    /**
     * Crea un nuevo tipo de equipo.
     * @param tipoEquipo Datos del tipo de equipo a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    public ResponseEntity<String> agregarTipoEquipo(@RequestBody TipoEquipo tipoEquipo) {
        try {
            tipoEquipoService.save(tipoEquipo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Tipo Equipo creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un tipo de equipo existente.
     * @param id ID del tipo de equipo a actualizar
     * @param tipoEquipo Datos actualizados del tipo de equipo
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarTipoEquipo(@PathVariable long id, @RequestBody TipoEquipo tipoEquipo) {
        try {
            TipoEquipo nuevoTipoEquipo = tipoEquipoService.update(tipoEquipo, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tipo Equipo no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un tipo de equipo del sistema.
     * @param id ID del tipo de equipo a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTipoEquipo(@PathVariable long id) {
        try {
            tipoEquipoService.delete(id);
            return ResponseEntity.ok("Tipo Equipo eliminado con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tipo Equipo no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }
}
