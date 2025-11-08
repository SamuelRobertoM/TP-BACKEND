package utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Element {
    private String status;       // "OK", "NOT_FOUND", "ZERO_RESULTS", etc.
    private Distance distance;
    private Duration duration;
}
