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
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.CamionUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DisponibilidadDTO;
import utn.frc.isi.backend.tpi_Integrador.services.CamionService;

import java.util.List;

@Tag(name = "Camiones", description = "API de gestión de camiones de la flota")
@RestController
@RequestMapping("/api/camiones")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @Operation(summary = "Obtener todos los camiones", 
               description = "Devuelve una lista completa de todos los camiones registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de camiones devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = CamionDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<CamionDTO>> obtenerTodos() {
        List<CamionDTO> camiones = camionService.obtenerTodos();
        return ResponseEntity.ok(camiones);
    }

    @Operation(summary = "Obtener un camión por ID", 
               description = "Busca y devuelve un camión específico mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Camión encontrado y devuelto",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = CamionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Camión no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CamionDTO> obtenerPorId(@PathVariable Long id) {
        return camionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo camión", 
               description = "Registra un nuevo camión en el sistema con todos sus datos técnicos y de operación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Camión creado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = CamionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PostMapping
    public ResponseEntity<CamionDTO> crearCamion(@Valid @RequestBody CamionCreateDTO camionCreateDTO) {
        CamionDTO nuevoCamion = camionService.crearCamion(camionCreateDTO);
        return ResponseEntity.status(201).body(nuevoCamion);
    }

    @Operation(summary = "Actualizar un camión existente", 
               description = "Modifica los datos de un camión registrado en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Camión actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = CamionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Camión no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CamionDTO> actualizarCamion(
            @PathVariable Long id, 
            @Valid @RequestBody CamionUpdateDTO camionUpdateDTO) {
        CamionDTO camionActualizado = camionService.actualizarCamion(id, camionUpdateDTO);
        if (camionActualizado != null) {
            return ResponseEntity.ok(camionActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener camiones disponibles con filtros", 
               description = "Busca camiones disponibles para asignación, opcionalmente filtrando por capacidad mínima de peso y/o volumen")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de camiones disponibles devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = CamionDTO.class)))
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<CamionDTO>> obtenerCamionesDisponibles(
            @RequestParam(required = false) Double pesoMinimo,
            @RequestParam(required = false) Double volumenMinimo) {

        List<CamionDTO> camionesDisponibles = camionService.buscarDisponibles(pesoMinimo, volumenMinimo);
        return ResponseEntity.ok(camionesDisponibles);
    }

    @Operation(summary = "Eliminar un camión", 
               description = "Elimina un camión del sistema de forma permanente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Camión eliminado exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Camión no encontrado",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable Long id) {
        camionService.eliminarCamion(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Actualizar disponibilidad de un camión", 
               description = "Modifica el estado de disponibilidad de un camión. Usado por servicio-operaciones para liberar camiones al finalizar tramos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = CamionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Camión no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de disponibilidad inválidos",
                     content = @Content)
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<CamionDTO> actualizarDisponibilidadCamion(
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadDTO disponibilidadDTO) {

        return camionService.actualizarDisponibilidad(id, disponibilidadDTO.getDisponible())
                .map(ResponseEntity::ok) // Si el servicio devuelve el DTO, responde 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si devuelve Optional vacío, responde 404
    }
}