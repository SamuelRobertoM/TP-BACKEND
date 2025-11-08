package com.tpi.gateway.config;

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
 * Configuración de OpenAPI/Swagger para el API Gateway.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configuración global de OpenAPI para el Gateway
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TPI Backend - API Gateway")
                        .version("1.0.0")
                        .description("""
                                API Gateway unificado para el sistema de gestión de transporte.
                                
                                Este gateway centraliza el acceso a todos los microservicios:
                                - **Servicio Flota**: Gestión de camiones, depósitos y tarifas
                                - **Servicio Operaciones**: Gestión de clientes, solicitudes y seguimiento
                                
                                ## Autenticación
                                Utiliza OAuth2/JWT con Keycloak. Para obtener un token:
                                
                                ```bash
                                curl -X POST "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \\
                                  -H "Content-Type: application/x-www-form-urlencoded" \\
                                  -d "client_id=postman-client" \\
                                  -d "username=admin" \\
                                  -d "password=admin123" \\
                                  -d "grant_type=password"
                                ```
                                
                                ## Usuarios de Prueba
                                - **admin / admin123** - Rol: ADMIN, FLOTA_MANAGER
                                - **operador.flota / flota123** - Rol: FLOTA_MANAGER
                                - **operador.operaciones / operaciones123** - Rol: OPERACIONES_MANAGER
                                """)
                        .contact(new Contact()
                                .name("Equipo TPI")
                                .email("soporte@tpi.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Gateway - Desarrollo Local")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT obtenido de Keycloak")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
