package com.SAFE_Rescue.API_Turno.controller;

import com.SAFE_Rescue.API_Turno.modelo.Compania;
import com.SAFE_Rescue.API_Turno.service.CompaniaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST para la gestión de compañías de bomberos.
 * Proporciona endpoints para operaciones CRUD de compañías.
 */
@RestController
@RequestMapping("/api-turnos/v1/companias")
public class CompaniaController {

    @Autowired
    private CompaniaService companiaService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todas las compañías registradas en el sistema.
     * @return ResponseEntity con lista de compañías o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    @Operation(summary = "Obtener todas las compañías", description = "Devuelve una lista de todas las compañías registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de compañías encontrada"),
            @ApiResponse(responseCode = "204", description = "No hay compañías registradas")
    })
    public ResponseEntity<List<Compania>> listarCompania() {
        List<Compania> compania = companiaService.findAll();
        if (compania.isEmpty()) {
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
    @Operation(summary = "Buscar compañía por ID", description = "Devuelve una compañía específica dada su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compañía encontrada"),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada")
    })
    public ResponseEntity<?> buscarCompania(@PathVariable int id) {
        Compania compania;
        try {
            compania = companiaService.findByID(id);
        } catch (NoSuchElementException e) {
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
    @Operation(summary = "Crear nueva compañía", description = "Crea una nueva compañía en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compañía creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    @Operation(summary = "Actualizar compañía", description = "Actualiza la información de una compañía existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compañía actualizada con éxito"),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> actualizarCompania(@PathVariable Integer id, @RequestBody Compania compania) {
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
    @Operation(summary = "Eliminar compañía", description = "Elimina una compañía del sistema dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compañía eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "Compañía no encontrada"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> eliminarCompania(@PathVariable Integer id) {
        try {
            companiaService.delete(id);
            return ResponseEntity.ok("Compania eliminada con éxito.");
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

    // GESTIÓN DE RELACIONES

    /**
     * Asigna una ubicación a una compañía.
     * @param companiaId ID de la Compañía
     * @param ubicacionId ID de la Ubicación
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{companiaId}/asignar-ubicacion/{ubicacionId}")
    @Operation(summary = "Asignar ubicación a compañía", description = "Asigna una ubicación a una compañía específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ubicación asignada a la compañía exitosamente"),
            @ApiResponse(responseCode = "404", description = "Compañía o ubicación no encontrada"),
            @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    public ResponseEntity<String> asignarUbicacion(@PathVariable Integer companiaId, @PathVariable Integer ubicacionId) {
        try {
            companiaService.asignarUbicacion(companiaId, ubicacionId);
            return ResponseEntity.ok("Ubicacion asignada a la Compania exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}