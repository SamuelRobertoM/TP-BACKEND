package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.Data;

/**
 * DTO para RF#5: Consultar contenedores pendientes de asignación a transporte
 * Representa un contenedor que aún no ha sido asignado a ningún viaje
 */
@Data
public class ContenedorPendienteDTO {
    private Long id;
    private String numero;
    private String estado;
    private String ubicacionActual;
    private String cliente;
    private Long solicitudId;
}
