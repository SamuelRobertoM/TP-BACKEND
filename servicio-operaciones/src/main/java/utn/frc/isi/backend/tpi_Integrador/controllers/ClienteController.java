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
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteCreateDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteDTO;
import utn.frc.isi.backend.tpi_Integrador.dtos.ClienteUpdateDTO;
import utn.frc.isi.backend.tpi_Integrador.services.ClienteService;

import java.util.List;

@Tag(name = "Clientes", description = "API de gestión de clientes del sistema de transporte")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Obtener todos los clientes", 
               description = "Devuelve una lista completa de todos los clientes registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes devuelta exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ClienteDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodos() {
        List<ClienteDTO> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Obtener un cliente por ID", 
               description = "Busca y devuelve un cliente específico mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado y devuelto",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo cliente", 
               description = "Registra un nuevo cliente en el sistema con sus datos de contacto y facturación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteCreateDTO clienteCreateDTO) {
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteCreateDTO);
        return ResponseEntity.status(201).body(nuevoCliente);
    }

    @Operation(summary = "Actualizar un cliente existente", 
               description = "Modifica los datos de un cliente registrado en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable Long id, 
            @Valid @RequestBody ClienteUpdateDTO clienteUpdateDTO) {
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteUpdateDTO);
        if (clienteActualizado != null) {
            return ResponseEntity.ok(clienteActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un cliente", 
               description = "Elimina un cliente del sistema de forma permanente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente",
                     content = @Content),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}