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
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.DepositoUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.DepositoService;

import java.util.List;

@Tag(name = "Depósitos", description = "API de gestión de depósitos y centros de distribución")
@RestController
@RequestMapping("/api/depositos")
public class DepositoController {

    private final DepositoService depositoService;

    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @Operation(summary = "Obtener todos los depósitos", 
               description = "Devuelve una lista completa de todos los depósitos registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de depósitos devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = DepositoDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<DepositoDTO>> obtenerTodos() {
        List<DepositoDTO> depositos = depositoService.obtenerTodos();
        return ResponseEntity.ok(depositos);
    }

    @Operation(summary = "Obtener un depósito por ID", 
               description = "Busca y devuelve un depósito específico mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Depósito encontrado y devuelto",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = DepositoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Depósito no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepositoDTO> obtenerPorId(@PathVariable Long id) {
        return depositoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo depósito", 
               description = "Registra un nuevo depósito en el sistema con su ubicación y datos operativos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Depósito creado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = DepositoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PostMapping
    public ResponseEntity<DepositoDTO> crearDeposito(@Valid @RequestBody DepositoCreateDTO depositoCreateDTO) {
        DepositoDTO nuevoDeposito = depositoService.crearDeposito(depositoCreateDTO);
        return ResponseEntity.status(201).body(nuevoDeposito);
    }

    @Operation(summary = "Actualizar un depósito existente", 
               description = "Modifica los datos de un depósito registrado en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Depósito actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = DepositoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Depósito no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepositoDTO> actualizarDeposito(
            @PathVariable Long id, 
            @Valid @RequestBody DepositoUpdateDTO depositoUpdateDTO) {
        DepositoDTO depositoActualizado = depositoService.actualizarDeposito(id, depositoUpdateDTO);
        if (depositoActualizado != null) {
            return ResponseEntity.ok(depositoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un depósito", 
               description = "Elimina un depósito del sistema de forma permanente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Depósito eliminado exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Depósito no encontrado",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeposito(@PathVariable Long id) {
        depositoService.eliminarDeposito(id);
        return ResponseEntity.noContent().build();
    }
}