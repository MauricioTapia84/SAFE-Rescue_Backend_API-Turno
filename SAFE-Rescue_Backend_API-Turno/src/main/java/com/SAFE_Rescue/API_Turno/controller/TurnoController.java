package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.service.TurnoService;
import com.SAFE_Rescue.API_Turno.modelo.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api-ciudadano/v1/ciudadanos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    public ResponseEntity<List<Turno>> listar(){

        List<Turno> turnos = turnoService.findAll();
        if(turnos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(turnos);
    }

    @PostMapping
    public ResponseEntity<String> agregarCiudadano(@RequestBody Turno turno) {
        try {

            turnoService.validarCiudadano(turno);
            Turno nuevoTurno = turnoService.save(turno);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ciudadano creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCiudadano(@PathVariable long id) {
        Turno turno;

        try {
            turno = turnoService.findByID(id);
        }catch(NoSuchElementException e){
            return new ResponseEntity<String>("Ciudadano no encontrado", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(turno);

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCiudadano(@PathVariable long id, @RequestBody Turno turno) {
        try {
            Turno nuevoTurno = turnoService.update(turno, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ciudadano no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCiudadano(@PathVariable long id) {

        try {
            turnoService.delete(id);
            return ResponseEntity.ok("Ciudadano eliminado con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ciudadano no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    @PostMapping("/{ciudadanoId}/asignar-credencial/{credencialId}")
    public ResponseEntity<String> asignarCredencial(@PathVariable int ciudadanoId, @PathVariable int credencialId) {
        try {
            turnoService.asignarCredencial(ciudadanoId, credencialId);
            return ResponseEntity.ok("Equipo asignada al Ciudadano exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
