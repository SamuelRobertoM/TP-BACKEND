package utn.frc.isi.backend.tpi_Integrador.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para el Servicio de Flota.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configuración global de OpenAPI para el Servicio de Flota
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TPI Backend - Servicio de Flota")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión de flota de transporte.
                                
                                Este servicio maneja:
                                - **Camiones**: Gestión de vehículos de la flota
                                - **Depósitos**: Gestión de depósitos y almacenes
                                - **Tarifas**: Configuración de tarifas de transporte
                                - **Disponibilidad**: Consulta de camiones disponibles
                                
                                ## Autenticación
                                Utiliza OAuth2/JWT con Keycloak. Para obtener un token:
                                
                                ```bash
                                curl -X POST "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \\
                                  -H "Content-Type: application/x-www-form-urlencoded" \\
                                  -d "client_id=tpi-api-gateway" \\
                                  -d "client_secret=tpi-gateway-secret-2024-secure" \\
                                  -d "username=admin" \\
                                  -d "password=admin123" \\
                                  -d "grant_type=password"
                                ```
                                
                                Luego use el token en el header: `Authorization: Bearer {token}`
                                """)
                        .contact(new Contact()
                                .name("TPI Backend Team")
                                .email("tpi@utn.edu.ar"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor de Desarrollo - Directo"),
                        new Server()
                                .url("http://localhost:8080/api/flota")
                                .description("Servidor de Desarrollo - Via API Gateway")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("JWT token obtenido de Keycloak")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
