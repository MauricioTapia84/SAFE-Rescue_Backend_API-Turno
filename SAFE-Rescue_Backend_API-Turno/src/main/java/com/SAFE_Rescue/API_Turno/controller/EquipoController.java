package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.Equipo;
import com.SAFE_Rescue.API_Turno.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de equipos de emergencia.
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de equipos.
 */
@RestController
@RequestMapping("/api-turnos/v1/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los equipos registrados en el sistema.
     * @return ResponseEntity con lista de equipos o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    public ResponseEntity<List<Equipo>> listar(){

        List<Equipo> equipos = equipoService.findAll();
        if(equipos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(equipos);
    }

    /**
     * Busca un equipo por su ID.
     * @param id ID del equipo a buscar
     * @return ResponseEntity con el equipo encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEquipo(@PathVariable long id) {
        Equipo equipo;

        try {
            equipo = equipoService.findByID(id);
        }catch(NoSuchElementException e){
            return new ResponseEntity<String>("Equipo no encontrado", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(equipo);

    }

    /**
     * Crea un nuevo equipo.
     * @param equipo Datos del equipo a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    public ResponseEntity<String> agregarEquipo(@RequestBody Equipo equipo) {
        try {
            equipoService.save(equipo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Equipo creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un equipo existente.
     * @param id ID del equipo a actualizar
     * @param equipo Datos actualizados del equipo
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEquipo(@PathVariable long id, @RequestBody Equipo equipo) {
        try {
            Equipo nuevoEquipo = equipoService.update(equipo, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Equipo no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un equipo del sistema.
     * @param id ID del equipo a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEquipo(@PathVariable long id) {

        equipoService.delete(id);
        return ResponseEntity.ok("Equipo eliminado con éxito.");
    }


    // GESTIÓN DE RELACIONES

    /**
     * Asigna una compañía a un equipo.
     * @param equipoId ID del equipo
     * @param companiaId ID de la compañía a asignar
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{equipoId}/asignar-compania/{companiaId}")
    public ResponseEntity<String> asignarCompania(@PathVariable int equipoId, @PathVariable int companiaId) {
        try {
            equipoService.asignarCompania(equipoId, companiaId);
            return ResponseEntity.ok("Compañia asignada al Equipo exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Asigna un turno a un equipo.
     * @param equipoId ID del equipo
     * @param turnoId ID del turno a asignar
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{equipoId}/asignar-turno/{turnoId}")
    public ResponseEntity<String> asignarTurno(@PathVariable int equipoId, @PathVariable int turnoId) {
        try {
            equipoService.asignarTurno(equipoId, turnoId);
            return ResponseEntity.ok("Turno asignado al Equipo exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Asigna un tipo de equipo a un equipo.
     * @param equipoId ID del equipo
     * @param tipoEquipoId ID del tipo de equipo a asignar
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{equipoId}/asignar-tipo-equipo/{tipoEquipoId}")
    public ResponseEntity<String> asignarTipoEquipo(@PathVariable int equipoId, @PathVariable int tipoEquipoId) {
        try {
            equipoService.asignarTipoEquipo(equipoId, tipoEquipoId);
            return ResponseEntity.ok("Tipo Equipo asignado al Equipo exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Asigna una lista de bomberos a un equipo.
     * @param equipoId ID del equipo
     * @param bomberosId Lista de IDs de bomberos a asignar
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{equipoId}/asignar-personal/{bomberosId}")
    public ResponseEntity<String> asignaPersonal(@PathVariable int equipoId, @PathVariable List<Long> bomberosId) {
        try {
            equipoService.asignarListaBomberos(equipoId, bomberosId);
            return ResponseEntity.ok("Lista bomberos asignada al Equipo exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
