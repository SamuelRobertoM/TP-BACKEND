package com.tpi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Aplicación principal del API Gateway para el sistema TPI.
 * 
 * Este gateway actúa como punto de entrada único para todos los microservicios,
 * manejando:
 * - Enrutamiento a servicio-flota y servicio-operaciones
 * - Autenticación y autorización mediante JWT de Keycloak
 * - CORS
 * - Circuit Breaker para resiliencia
 * - Rate Limiting
 * 
 * @author TPI Backend Team
 * @version 1.0.0
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
