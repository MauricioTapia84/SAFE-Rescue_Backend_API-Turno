package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.service.CompaniaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de compañías de bomberos.
 * Proporciona endpoints para operaciones CRUD de compañías.
 */
@RestController
@RequestMapping("/api-turnos/v1/companias")
public class CompaniaController {

    @Autowired
    private CompaniaService companiaService;

    /**
     * Obtiene todas las compañías registradas en el sistema.
     * @return ResponseEntity con lista de compañías o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    public ResponseEntity<List<Compania>> listarCompania(){

        List<Compania> compania = companiaService.findAll();
        if(compania.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(compania);
    }

    /**
     * Busca una compañía por su ID.
     * @param id ID de la compañía a buscar
     * @return ResponseEntity con la compañía encontrada o mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCompania(@PathVariable int id) {
        Compania compania;

        try {
            compania = companiaService.findByID(id);
        }catch(NoSuchElementException e){
            return new ResponseEntity<String>("Compania no encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(compania);

    }

    /**
     * Crea una nueva compañía.
     * @param compania Datos de la compañía a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    public ResponseEntity<String> agregarCompania(@RequestBody Compania compania) {
        try {
            companiaService.save(compania);
            return ResponseEntity.status(HttpStatus.CREATED).body("Compania creada con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza una compañía existente.
     * @param id ID de la compañía a actualizar
     * @param compania Datos actualizados de la compañía
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCompania(@PathVariable long id, @RequestBody Compania compania) {
        try {
            Compania nuevoCompania = companiaService.update(compania, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Compania no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    /**
     * Elimina una compañía del sistema.
     * @param id ID de la compañía a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCompania(@PathVariable long id) {
        companiaService.delete(id);
        return ResponseEntity.ok("Compania eliminada con éxito.");
    }

    /**
     * Asigna una ubicacion a una compania.
     * @param companiaId ID de la Compania
     * @param ubicacionId ID de la Ubicaion
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{companiaId}/asignar-ubicacion/{ubicacionId}")
    public ResponseEntity<String> asignarUbicacion(@PathVariable Long companiaId, @PathVariable Long ubicacionId) {
        try {
            companiaService.asignarUbicacion(companiaId, ubicacionId);
            return ResponseEntity.ok("Ubicacion asignada a la Compania exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
