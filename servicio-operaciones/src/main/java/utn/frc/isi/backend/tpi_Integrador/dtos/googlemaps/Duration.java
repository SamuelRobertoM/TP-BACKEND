package utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Duration {
    private String text;   // e.g., "7 hours 30 mins"
    private Long value;    // Duration in seconds
}
