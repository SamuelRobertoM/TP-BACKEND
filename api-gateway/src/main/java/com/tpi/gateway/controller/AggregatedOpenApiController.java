package com.tpi.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para agregar las definiciones OpenAPI de todos los microservicios
 */
@RestController
public class AggregatedOpenApiController {

    private final WebClient webClient;

    public AggregatedOpenApiController() {
        this.webClient = WebClient.builder().build();
    }

    /**
     * Endpoint que combina las definiciones OpenAPI de flota y operaciones
     */
    @GetMapping(value = "/v3/api-docs/aggregate", produces = "application/json")
    public Mono<Map<String, Object>> getAggregatedApiDocs() {
        
        // Obtener definiciones de ambos servicios
        Mono<Map> flotaDocs = webClient.get()
                .uri("http://localhost:8081/v3/api-docs")
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorReturn(new HashMap<>());
        
        Mono<Map> operacionesDocs = webClient.get()
                .uri("http://localhost:8082/v3/api-docs")
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorReturn(new HashMap<>());
        
        // Combinar ambas definiciones
        return Mono.zip(flotaDocs, operacionesDocs)
                .map(tuple -> {
                    Map<String, Object> aggregated = new HashMap<>();
                    Map<String, Object> flota = tuple.getT1();
                    Map<String, Object> operaciones = tuple.getT2();
                    
                    // Información básica
                    aggregated.put("openapi", "3.0.1");
                    
                    Map<String, Object> info = new HashMap<>();
                    info.put("title", "TPI Backend - Todos los Servicios");
                    info.put("description", "API Gateway con todos los endpoints de Flota y Operaciones agregados");
                    info.put("version", "1.0.0");
                    aggregated.put("info", info);
                    
                    // Combinar paths
                    Map<String, Object> allPaths = new HashMap<>();
                    
                    // Agregar paths de flota con prefijo /api/flota
                    if (flota.containsKey("paths")) {
                        Map<String, Object> flotaPaths = (Map<String, Object>) flota.get("paths");
                        flotaPaths.forEach((path, value) -> {
                            // Cambiar /api/camiones a /api/flota/camiones
                            String newPath = path.replace("/api/", "/api/flota/");
                            allPaths.put(newPath, value);
                        });
                    }
                    
                    // Agregar paths de operaciones con prefijo /api/operaciones
                    if (operaciones.containsKey("paths")) {
                        Map<String, Object> operacionesPaths = (Map<String, Object>) operaciones.get("paths");
                        operacionesPaths.forEach((path, value) -> {
                            // Cambiar /api/clientes a /api/operaciones/clientes
                            String newPath = path.replace("/api/", "/api/operaciones/");
                            allPaths.put(newPath, value);
                        });
                    }
                    
                    aggregated.put("paths", allPaths);
                    
                    // Combinar components/schemas
                    Map<String, Object> components = new HashMap<>();
                    Map<String, Object> schemas = new HashMap<>();
                    
                    if (flota.containsKey("components")) {
                        Map<String, Object> flotaComponents = (Map<String, Object>) flota.get("components");
                        if (flotaComponents.containsKey("schemas")) {
                            schemas.putAll((Map<String, Object>) flotaComponents.get("schemas"));
                        }
                    }
                    
                    if (operaciones.containsKey("components")) {
                        Map<String, Object> operacionesComponents = (Map<String, Object>) operaciones.get("components");
                        if (operacionesComponents.containsKey("schemas")) {
                            schemas.putAll((Map<String, Object>) operacionesComponents.get("schemas"));
                        }
                    }
                    
                    // Agregar security scheme para JWT
                    Map<String, Object> securitySchemes = new HashMap<>();
                    Map<String, Object> bearerAuth = new HashMap<>();
                    bearerAuth.put("type", "http");
                    bearerAuth.put("scheme", "bearer");
                    bearerAuth.put("bearerFormat", "JWT");
                    bearerAuth.put("description", "Ingrese el token JWT obtenido de Keycloak");
                    securitySchemes.put("bearer-jwt", bearerAuth);
                    
                    components.put("schemas", schemas);
                    components.put("securitySchemes", securitySchemes);
                    aggregated.put("components", components);
                    
                    // Agregar seguridad global - todos los endpoints requieren JWT
                    Map<String, Object> securityRequirement = new HashMap<>();
                    securityRequirement.put("bearer-jwt", new java.util.ArrayList<>());
                    aggregated.put("security", java.util.List.of(securityRequirement));
                    
                    return aggregated;
                });
    }
}
