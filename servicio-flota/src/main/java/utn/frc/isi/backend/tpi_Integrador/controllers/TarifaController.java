package utn.frc.isi.backend.tpi_Integrador.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TarifaDTO;
import utn.frc.isi.backend.tpi_Integrador.services.TarifaService;

import java.util.List;

@Tag(name = "Tarifas", description = "API de gestión de tarifas y costos de transporte")
@RestController
@RequestMapping("/api/tarifas")
@CrossOrigin(origins = "*")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @Operation(summary = "Obtener la tarifa activa vigente", 
               description = "Devuelve la tarifa actualmente en vigor para cálculos de costos de transporte")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarifa activa encontrada y devuelta",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TarifaDTO.class))),
        @ApiResponse(responseCode = "404", description = "No existe tarifa activa en el sistema",
                     content = @Content)
    })
    @GetMapping("/actual")
    public ResponseEntity<TarifaDTO> obtenerTarifaActiva() {
        return tarifaService.obtenerTarifaActivaDTO()
                .map(tarifaDTO -> ResponseEntity.ok(tarifaDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener todas las tarifas", 
               description = "Devuelve el histórico completo de tarifas ordenadas por fecha de vigencia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tarifas devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TarifaDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<TarifaDTO>> obtenerTodas() {
        List<TarifaDTO> tarifas = tarifaService.obtenerTodasDTO();
        return ResponseEntity.ok(tarifas);
    }

    @Operation(summary = "Obtener una tarifa por ID", 
               description = "Busca y devuelve una tarifa específica mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarifa encontrada y devuelta",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TarifaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO> obtenerPorId(@PathVariable Long id) {
        return tarifaService.obtenerPorIdDTO(id)
                .map(tarifaDTO -> ResponseEntity.ok(tarifaDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva tarifa", 
               description = "Registra una nueva tarifa en el sistema con todos los costos de operación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarifa creada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TarifaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o reglas de negocio violadas",
                     content = @Content)
    })
    @PostMapping
    public ResponseEntity<TarifaDTO> crearTarifa(@Valid @RequestBody TarifaCreateDTO createDTO) {
        try {
            TarifaDTO nuevaTarifa = tarifaService.crearTarifaFromDTO(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarifa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar una tarifa existente", 
               description = "Modifica los datos de una tarifa registrada en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarifa actualizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TarifaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o reglas de negocio violadas",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TarifaDTO> actualizarTarifa(@PathVariable Long id, 
                                                      @Valid @RequestBody TarifaUpdateDTO updateDTO) {
        try {
            return tarifaService.actualizarTarifaFromDTO(id, updateDTO)
                    .map(tarifaActualizada -> ResponseEntity.ok(tarifaActualizada))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Eliminar una tarifa", 
               description = "Elimina una tarifa del sistema de forma permanente. Solo se puede eliminar si no está activa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tarifa eliminada exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar una tarifa activa",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id) {
        try {
            if (tarifaService.eliminarTarifa(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            // Tarifa activa no se puede eliminar
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Verificar existencia de tarifa activa", 
               description = "Endpoint de utilidad que verifica si existe una tarifa activa en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping("/existe-activa")
    public ResponseEntity<Boolean> existeTarifaActiva() {
        boolean existe = tarifaService.existeTarifaActiva();
        return ResponseEntity.ok(existe);
    }
}