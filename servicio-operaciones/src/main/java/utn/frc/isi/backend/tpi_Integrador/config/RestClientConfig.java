package utn.frc.isi.backend.tpi_Integrador.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${google.maps.base-url}")
    private String googleMapsBaseUrl;

    @Value("${servicio-flota.base-url}")
    private String servicioFlotaBaseUrl;

    @Bean
    public RestClient googleMapsRestClient() {
        return RestClient.builder()
                .baseUrl(googleMapsBaseUrl)
                .build();
    }

    @Bean
    public RestClient flotaRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(servicioFlotaBaseUrl)
                .build();
    }
}
