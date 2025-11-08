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
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorEstadoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorPendienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ContenedorUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.ContenedorService;

import java.util.List;

@Tag(name = "Contenedores", description = "API de gestión de contenedores y seguimiento de envíos")
@RestController
@RequestMapping("/api/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    @Operation(summary = "Obtener todos los contenedores", 
               description = "Devuelve una lista completa de todos los contenedores registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contenedores devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ContenedorDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ContenedorDTO>> obtenerTodos() {
        List<ContenedorDTO> contenedores = contenedorService.obtenerTodos();
        return ResponseEntity.ok(contenedores);
    }

    @Operation(summary = "Obtener un contenedor por ID", 
               description = "Busca y devuelve un contenedor específico mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contenedor encontrado y devuelto",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ContenedorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contenedor no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContenedorDTO> obtenerPorId(@PathVariable Long id) {
        return contenedorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo contenedor", 
               description = "Registra un nuevo contenedor en el sistema con sus especificaciones técnicas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contenedor creado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ContenedorDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PostMapping
    public ResponseEntity<ContenedorDTO> crearContenedor(@Valid @RequestBody ContenedorCreateDTO contenedorCreateDTO) {
        ContenedorDTO nuevoContenedor = contenedorService.crearContenedor(contenedorCreateDTO);
        return ResponseEntity.status(201).body(nuevoContenedor);
    }

    @Operation(summary = "Actualizar un contenedor existente", 
               description = "Modifica los datos de un contenedor registrado en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contenedor actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ContenedorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contenedor no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContenedorDTO> actualizarContenedor(
            @PathVariable Long id, 
            @Valid @RequestBody ContenedorUpdateDTO contenedorUpdateDTO) {
        ContenedorDTO contenedorActualizado = contenedorService.actualizarContenedor(id, contenedorUpdateDTO);
        if (contenedorActualizado != null) {
            return ResponseEntity.ok(contenedorActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un contenedor", 
               description = "Elimina un contenedor del sistema de forma permanente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contenedor eliminado exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Contenedor no encontrado",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContenedor(@PathVariable Long id) {
        contenedorService.eliminarContenedor(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Consultar estado de un contenedor (RF#2)", 
               description = "Permite al cliente consultar el estado actual y ubicación de su contenedor para seguimiento en tiempo real")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del contenedor devuelto exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ContenedorEstadoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contenedor no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}/estado")
    public ResponseEntity<ContenedorEstadoDTO> consultarEstadoContenedor(@PathVariable Long id) {
        return contenedorService.consultarEstado(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Consultar contenedores pendientes (RF#5)", 
               description = "Retorna la lista de contenedores que aún no han sido entregados y están disponibles para asignación a rutas de transporte")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contenedores pendientes devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ContenedorPendienteDTO.class)))
    })
    @GetMapping("/pendientes")
    public ResponseEntity<List<ContenedorPendienteDTO>> consultarContenedoresPendientes() {
        List<ContenedorPendienteDTO> pendientes = contenedorService.consultarPendientes();
        return ResponseEntity.ok(pendientes);
    }
}