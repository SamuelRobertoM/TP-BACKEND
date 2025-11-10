package utn.frc.isi.backend.tpi_Integrador.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
                .requestInterceptor((request, body, execution) -> {
                    // Propagar el token JWT del contexto de seguridad actual
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                        String token = jwtAuth.getToken().getTokenValue();
                        request.getHeaders().setBearerAuth(token);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
