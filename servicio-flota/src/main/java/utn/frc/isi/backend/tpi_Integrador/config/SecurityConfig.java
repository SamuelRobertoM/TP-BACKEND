package utn.frc.isi.backend.tpi_Integrador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuración de Spring Security para el Servicio de Flota.
 * 
 * Esta configuración:
 * - Valida JWT tokens emitidos por Keycloak
 * - Extrae roles del JWT
 * - Protege endpoints según roles
 * - Permite acceso a H2 Console y Swagger en desarrollo
 * 
 * @author TPI Backend Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Habilitar CORS
            .cors(cors -> cors.configure(http))
            
            // Deshabilitar CSRF ya que usamos JWT (stateless)
            .csrf(csrf -> csrf.disable())
            
            // Configuración de sesiones - stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configuración de autorización
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos - H2 Console y Actuator (solo para desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/**", "/actuator/health/**").permitAll()
                
                // Endpoints de documentación Swagger - permitir acceso SIN AUTENTICACIÓN
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs", "/v3/api-docs.yaml").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                
                // Endpoint público para consulta de tarifa activa (usado por otros microservicios)
                .requestMatchers(HttpMethod.GET, "/api/tarifas/actual").permitAll()
                
                // ===== Endpoints de API =====
                // Lectura (GET) - cualquier usuario autenticado
                .requestMatchers(HttpMethod.GET, "/api/**")
                    .hasAnyRole("ADMIN", "FLOTA_MANAGER", "OPERACIONES_MANAGER", "USER")
                
                // Creación (POST) - solo managers
                .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasAnyRole("ADMIN", "FLOTA_MANAGER")
                
                // Actualización (PUT) - solo managers
                .requestMatchers(HttpMethod.PUT, "/api/**")
                    .hasAnyRole("ADMIN", "FLOTA_MANAGER")
                
                // Eliminación (DELETE) - solo admin
                .requestMatchers(HttpMethod.DELETE, "/api/**")
                    .hasRole("ADMIN")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configuración de OAuth2 Resource Server con JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        
        // Permitir frames para H2 Console (solo desarrollo)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        
        return http.build();
    }

    /**
     * Decodificador de JWT que valida los tokens contra Keycloak.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // URL del endpoint de claves públicas de Keycloak
        String jwkSetUri = "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/certs";
        
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * Converter que extrae los roles del JWT y los convierte en authorities de Spring Security.
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
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
            
            // Extraer roles del resource_access para clientes específicos
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                // Buscar roles en diferentes clientes
                for (String clientId : List.of("servicio-flota", "tpi-api-gateway")) {
                    if (resourceAccess.containsKey(clientId)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);
                        if (client != null && client.containsKey("roles")) {
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
        
        return converter;
    }
}
