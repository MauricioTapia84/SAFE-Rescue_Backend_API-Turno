package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.Equipo;
import com.SAFE_Rescue.API_Turno.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api-ciudadano/v1/credenciales")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public ResponseEntity<List<Equipo>> listar(){

        List<Equipo> credenciales = equipoService.findAll();
        if(credenciales.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(credenciales);
    }

    @PostMapping
    public ResponseEntity<String> agregarCredencial(@RequestBody Equipo equipo) {
        try {
            equipoService.save(equipo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Equipo creada con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCredencial(@PathVariable long id) {
        Equipo equipo;

        try {
            equipo = equipoService.findByID(id);
        }catch(NoSuchElementException e){
            return new ResponseEntity<String>("Equipo no encontrada", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(equipo);

    }


    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCredencial(@PathVariable long id, @RequestBody Equipo equipo) {
        try {
            Equipo nuevoEquipo = equipoService.update(equipo, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Equipo no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCredencial(@PathVariable long id) {

        equipoService.delete(id);
        return ResponseEntity.ok("Equipo eliminada con éxito.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login) {
        boolean isAuthenticated = equipoService.verificarCredenciales(login.getCorreo(), login.getContrasenia());

        if (isAuthenticated) {
            return ResponseEntity.ok("Login exitoso");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }


}
