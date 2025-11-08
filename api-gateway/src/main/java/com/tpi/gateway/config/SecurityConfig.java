package com.tpi.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuración de Spring Security para el API Gateway.
 * 
 * Esta clase configura:
 * - Autenticación mediante JWT de Keycloak
 * - Autorización basada en roles
 * - CORS
 * - Endpoints públicos y protegidos
 * 
 * @author TPI Backend Team
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     * Define qué endpoints requieren autenticación y qué roles.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // Deshabilitar CSRF ya que usamos JWT (stateless)
            .csrf(csrf -> csrf.disable())
            
            // Configuración de autorización
            .authorizeExchange(exchanges -> exchanges
                // Endpoints públicos - no requieren autenticación
                .pathMatchers("/actuator/**", "/actuator/health/**").permitAll()
                
                // Swagger UI y OpenAPI - públicos para facilitar pruebas
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                
                // Endpoints de documentación - requieren autenticación
                .pathMatchers("/docs/**").authenticated()
                
                // ===== Servicio Flota =====
                // Lectura de flota - cualquier usuario autenticado
                .pathMatchers(HttpMethod.GET, "/api/flota/**")
                    .hasAnyRole("ADMIN", "FLOTA_MANAGER", "OPERACIONES_MANAGER", "USER")
                
                // Escritura en flota - solo managers
                .pathMatchers(HttpMethod.POST, "/api/flota/**")
                    .hasAnyRole("ADMIN", "FLOTA_MANAGER")
                .pathMatchers(HttpMethod.PUT, "/api/flota/**")
                    .hasAnyRole("ADMIN", "FLOTA_MANAGER")
                .pathMatchers(HttpMethod.DELETE, "/api/flota/**")
                    .hasRole("ADMIN")
                
                // ===== Servicio Operaciones =====
                // Lectura de operaciones - cualquier usuario autenticado
                .pathMatchers(HttpMethod.GET, "/api/operaciones/**")
                    .hasAnyRole("ADMIN", "OPERACIONES_MANAGER", "TRANSPORTISTA", "USER")
                
                // Escritura en operaciones - solo managers
                .pathMatchers(HttpMethod.POST, "/api/operaciones/**")
                    .hasAnyRole("ADMIN", "OPERACIONES_MANAGER")
                .pathMatchers(HttpMethod.PUT, "/api/operaciones/**")
                    .hasAnyRole("ADMIN", "OPERACIONES_MANAGER")
                .pathMatchers(HttpMethod.DELETE, "/api/operaciones/**")
                    .hasRole("ADMIN")
                
                // Cualquier otra petición requiere autenticación
                .anyExchange().authenticated()
            )
            
            // Configuración de OAuth2 Resource Server con JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
            );
        
        return http.build();
    }

    /**
     * Convierte el JWT de Keycloak en un objeto Authentication de Spring Security.
     * Extrae los roles del JWT y los convierte en GrantedAuthorities.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Extraer roles del realm_access
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                authorities.addAll(
                    roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList())
                );
            }
            
            // Extraer roles del resource_access para el cliente específico
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                // Intentar con diferentes nombres de cliente
                for (String clientId : List.of("tpi-api-gateway", "api-gateway", "servicio-flota", "servicio-operaciones")) {
                    if (resourceAccess.containsKey(clientId)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);
                        if (client.containsKey("roles")) {
                            @SuppressWarnings("unchecked")
                            List<String> clientRoles = (List<String>) client.get("roles");
                            authorities.addAll(
                                clientRoles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .collect(Collectors.toList())
                            );
                        }
                    }
                }
            }
            
            return authorities;
        });
        
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
