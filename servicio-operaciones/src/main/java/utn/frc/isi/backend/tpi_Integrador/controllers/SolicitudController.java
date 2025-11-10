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
import utn.frc.isi.backend.tpi_Integrador.dtos.FinalizacionSolicitudDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.RutaTentativaDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.SolicitudUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.RutaService;
import utn.frc.isi.backend.tpi_Integrador.services.SolicitudService;

import java.util.List;

@Tag(name = "Solicitudes", description = "API de gestión de solicitudes de transporte y logística")
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final RutaService rutaService;

    public SolicitudController(SolicitudService solicitudService, RutaService rutaService) {
        this.solicitudService = solicitudService;
        this.rutaService = rutaService;
    }

    @Operation(summary = "Obtener todas las solicitudes", 
               description = "Devuelve una lista completa de todas las solicitudes de transporte registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de solicitudes devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = SolicitudDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<SolicitudDTO>> obtenerTodas() {
        List<SolicitudDTO> solicitudes = solicitudService.obtenerTodas();
        return ResponseEntity.ok(solicitudes);
    }

    @Operation(summary = "Crear nueva solicitud de transporte (RF#1)", 
               description = "Endpoint principal del sistema que orquesta la creación completa de una solicitud de transporte incluyendo cliente, contenedor y ruta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = SolicitudDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o cliente no encontrado",
                     content = @Content)
    })
    @PostMapping
    public ResponseEntity<SolicitudDTO> crearSolicitud(@Valid @RequestBody SolicitudCreateDTO solicitudDTO) {
        try {
            SolicitudDTO nuevaSolicitud = solicitudService.crearNuevaSolicitud(solicitudDTO);
            return ResponseEntity.status(201).body(nuevaSolicitud);
        } catch (IllegalArgumentException e) {
            // Error de validación de datos de entrada
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Manejo básico de error si el cliente no se encuentra o hay errores de validación
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== ENDPOINTS ESPECÍFICOS (deben ir ANTES de /{id}) ==========
    
    @Operation(summary = "Consultar estado completo del transporte (RF#2)", 
               description = "Permite al cliente consultar el estado detallado de su solicitud, incluyendo ubicación del contenedor, progreso de tramos y tiempo estimado de llegada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la solicitud devuelto exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = SolicitudEstadoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                     content = @Content)
    })
    @GetMapping("/{id}/estado")
    public ResponseEntity<SolicitudEstadoDTO> consultarEstadoSolicitud(@PathVariable Long id) {
        return solicitudService.consultarEstadoSolicitud(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Consultar rutas tentativas (RF#3)", 
               description = "Calcula y devuelve opciones de rutas posibles con estimaciones de costo, tiempo y distancia para evaluar antes de la asignación definitiva")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rutas tentativas calculadas exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = RutaTentativaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada o sin ruta asociada",
                     content = @Content)
    })
    @GetMapping("/{solicitudId}/rutas/tentativas")
    public ResponseEntity<List<RutaTentativaDTO>> consultarRutasTentativas(@PathVariable Long solicitudId) {
        try {
            List<RutaTentativaDTO> rutas = rutaService.calcularRutasTentativas(solicitudId);
            return ResponseEntity.ok(rutas);
        } catch (RuntimeException e) {
            // Si la solicitud no existe o no tiene ruta asociada
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Asignar ruta definitiva a solicitud (RF#6)", 
               description = "Confirma una ruta tentativa como definitiva, crea los tramos de transporte y cambia el estado de la solicitud a PROGRAMADA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ruta asignada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = RutaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de ruta inválidos",
                     content = @Content)
    })
    @PostMapping("/{solicitudId}/asignar-ruta")
    public ResponseEntity<RutaDTO> asignarRuta(@PathVariable Long solicitudId, @Valid @RequestBody RutaCreateDTO rutaDTO) {
        try {
            RutaDTO rutaAsignada = rutaService.asignarRutaASolicitud(solicitudId, rutaDTO);
            return ResponseEntity.status(201).body(rutaAsignada);
        } catch (RuntimeException e) {
            // Si la solicitud no existe
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Finalizar solicitud (RF#9)", 
               description = "Finaliza una solicitud calculando el costo total y tiempo real. Solo se puede finalizar si todos los tramos están FINALIZADOS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud finalizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = SolicitudDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Estado incorrecto o tramos pendientes de finalizar",
                     content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content)
    })
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<SolicitudDTO> finalizarSolicitud(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid FinalizacionSolicitudDTO finalizacionDTO) {
        try {
            return solicitudService.finalizarSolicitud(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            // Error de validación (estado incorrecto, tramos pendientes, etc.)
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Otros errores
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== ENDPOINTS GENÉRICOS (deben ir DESPUÉS de los específicos) ==========

    @Operation(summary = "Obtener una solicitud por ID", 
               description = "Busca y devuelve una solicitud específica mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud encontrada y devuelta",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = SolicitudDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtenerPorId(@PathVariable Long id) {
        return solicitudService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una solicitud existente", 
               description = "Modifica los datos de una solicitud registrada en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud actualizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = SolicitudDTO.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudDTO> actualizarSolicitud(@PathVariable Long id, @Valid @RequestBody SolicitudUpdateDTO solicitud) {
        SolicitudDTO solicitudActualizada = solicitudService.actualizarSolicitud(id, solicitud);
        if (solicitudActualizada != null) {
            return ResponseEntity.ok(solicitudActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una solicitud", 
               description = "Elimina una solicitud del sistema de forma permanente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Solicitud eliminada exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        solicitudService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}