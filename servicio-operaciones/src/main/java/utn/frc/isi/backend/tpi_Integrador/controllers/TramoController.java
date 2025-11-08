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
import utn.frc.isi.backend.tpi_Integrador.dtos.AsignacionCamionDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.TramoDTO;
import utn.frc.isi.backend.tpi_Integrador.services.TramoService;

import java.util.List;

@Tag(name = "Tramos", description = "API de gestión de tramos de transporte, asignación de camiones y seguimiento de viajes")
@RestController
@RequestMapping("/api/tramos")
public class TramoController {

    private final TramoService tramoService;

    public TramoController(TramoService tramoService) {
        this.tramoService = tramoService;
    }

    @Operation(summary = "Obtener todos los tramos", 
               description = "Retorna la lista completa de tramos registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tramos retornada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TramoDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<TramoDTO>> obtenerTodos() {
        List<TramoDTO> tramos = tramoService.obtenerTodos();
        return ResponseEntity.ok(tramos);
    }

    @Operation(summary = "Obtener tramo por ID", 
               description = "Retorna los detalles de un tramo específico incluyendo depósitos, distancia, estado y camión asignado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tramo encontrado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TramoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tramo no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TramoDTO> obtenerPorId(@PathVariable Long id) {
        return tramoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Asignar camión a tramo (RF#6)", 
               description = "Asigna un camión disponible a un tramo específico. Valida capacidad del camión y disponibilidad antes de la asignación. Cambia el estado del tramo a ASIGNADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Camión asignado exitosamente al tramo",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TramoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tramo o camión no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Camión no disponible, capacidad insuficiente o estado del tramo inválido",
                     content = @Content)
    })
    @PostMapping("/{id}/asignar-camion")
    public ResponseEntity<TramoDTO> asignarCamion(@PathVariable Long id, @Valid @RequestBody AsignacionCamionDTO asignacionDTO) {
        try {
            TramoDTO tramoActualizado = tramoService.asignarCamion(id, asignacionDTO);
            return ResponseEntity.ok(tramoActualizado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request con el mensaje de error
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Iniciar tramo de transporte (RF#8)", 
               description = "El transportista marca el inicio del viaje. Actualiza el estado del tramo a INICIADO y el contenedor a EN_VIAJE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tramo iniciado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TramoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tramo no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "El tramo no está en estado ASIGNADO o no tiene camión asignado",
                     content = @Content)
    })
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<TramoDTO> iniciarTramo(@PathVariable Long id) {
        try {
            TramoDTO tramoIniciado = tramoService.iniciarTramo(id);
            return ResponseEntity.ok(tramoIniciado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request en caso de validación fallida
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Finalizar tramo de transporte (RF#8)", 
               description = "El transportista marca el fin del viaje. Calcula costo real consultando tarifas en servicio-flota. Si es el último tramo de la ruta, marca el contenedor como ENTREGADO. Libera el camión para nuevas asignaciones")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tramo finalizado exitosamente con costo calculado",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TramoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tramo no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "El tramo no está en estado INICIADO",
                     content = @Content),
        @ApiResponse(responseCode = "500", description = "Error al calcular costo o comunicarse con servicio-flota",
                     content = @Content)
    })
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<TramoDTO> finalizarTramo(@PathVariable Long id) {
        try {
            TramoDTO tramoFinalizado = tramoService.finalizarTramo(id);
            return ResponseEntity.ok(tramoFinalizado);
        } catch (RuntimeException e) {
            // Retornar 400 Bad Request en caso de validación fallida
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Obtener tramos asignados a transportista (RF#7)", 
               description = "Permite al transportista consultar todos sus tramos pendientes (estados ASIGNADO, INICIADO). Excluye tramos finalizados o cancelados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tramos asignados retornada (puede ser vacía)",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = TramoDTO.class)))
    })
    @GetMapping("/transportistas/{camionId}/tramos")
    public ResponseEntity<List<TramoDTO>> obtenerTramosParaTransportista(@PathVariable Long camionId) {
        List<TramoDTO> tramosAsignados = tramoService.obtenerTramosAsignadosTransportista(camionId);
        // Devuelve 200 OK con lista vacía si no hay tramos, o con los tramos encontrados
        return ResponseEntity.ok(tramosAsignados);
    }
}