package utn.frc.isi.backend.tpi_Integrador.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para representar un Tramo en las respuestas
 * Incluye información completa del tramo con referencias a camión y depósitos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoDTO {
    private Long id;
    private Long rutaId;
    private int orden;
    private String tipo;
    private String estado;
    private String puntoInicio;
    private double latitudInicio;
    private double longitudInicio;
    private String puntoFin;
    private double latitudFin;
    private double longitudFin;
    private double distanciaKm;
    private int tiempoEstimadoHoras;
    private LocalDateTime fechaEstimadaInicio;
    private LocalDateTime fechaEstimadaFin;
    private LocalDateTime fechaRealInicio;
    private LocalDateTime fechaRealFin;
    private double costoReal;
    private CamionReferenceDTO camion;
    private DepositoReferenceDTO depositoOrigen;
    private DepositoReferenceDTO depositoDestino;
}
