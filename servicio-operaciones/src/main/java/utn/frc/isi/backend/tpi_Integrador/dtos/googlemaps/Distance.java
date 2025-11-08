package utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Distance {
    private String text;   // e.g., "647 km"
    private Long value;    // Distance in meters
}
