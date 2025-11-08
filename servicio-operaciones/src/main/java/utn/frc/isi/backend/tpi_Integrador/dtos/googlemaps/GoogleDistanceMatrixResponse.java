package utn.frc.isi.backend.tpi_Integrador.dtos.googlemaps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleDistanceMatrixResponse {
    private String status;               // "OK", "INVALID_REQUEST", "MAX_ELEMENTS_EXCEEDED", etc.
    
    @JsonProperty("origin_addresses")
    private List<String> originAddresses;
    
    @JsonProperty("destination_addresses")
    private List<String> destinationAddresses;
    
    private List<Row> rows;
}
