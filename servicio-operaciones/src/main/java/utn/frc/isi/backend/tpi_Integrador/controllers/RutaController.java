package utn.frc.isi.backend.tpi_Integrador.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.RutaService;

import java.util.List;

@Tag(name = "Rutas", description = "API de gestión de rutas de transporte y planificación de trayectos")
@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @Operation(summary = "Obtener todas las rutas", 
               description = "Retorna la lista completa de rutas registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de rutas retornada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = RutaDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<RutaDTO>> obtenerTodas() {
        List<RutaDTO> rutas = rutaService.obtenerTodas();
        return ResponseEntity.ok(rutas);
    }

    @Operation(summary = "Obtener ruta por ID", 
               description = "Retorna los detalles de una ruta específica incluyendo depósitos de origen/destino y distancia total")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruta encontrada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = RutaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Ruta no encontrada",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RutaDTO> obtenerPorId(@PathVariable Long id) {
        return rutaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Nota: Las rutas se crean mediante POST /api/solicitudes/{solicitudId}/asignar-ruta (RF#4)
     * No hay endpoint POST /api/rutas directo
     */

    @Operation(summary = "Actualizar ruta", 
               description = "Actualiza los datos de una ruta existente como distancia total o depósitos de origen/destino")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruta actualizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = RutaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Ruta no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de ruta inválidos",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RutaDTO> actualizarRuta(@PathVariable Long id, @Valid @RequestBody RutaUpdateDTO ruta) {
        RutaDTO rutaActualizada = rutaService.actualizarRuta(id, ruta);
        if (rutaActualizada != null) {
            return ResponseEntity.ok(rutaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar ruta", 
               description = "Elimina una ruta del sistema. No se debe eliminar si tiene tramos asociados activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ruta eliminada exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Ruta no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Ruta no puede ser eliminada (tiene tramos asociados)",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}