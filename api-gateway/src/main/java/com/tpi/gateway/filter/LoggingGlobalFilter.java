package com.tpi.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Filtro global para logging de todas las requests que pasan por el Gateway.
 * 
 * Este filtro:
 * - Genera un ID único de correlación para cada request
 * - Logea información de la request (método, path, usuario)
 * - Propaga headers de autenticación
 * - Mide el tiempo de respuesta
 * 
 * @author TPI Backend Team
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();
        
        // Agregar request ID al header para trazabilidad
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-Request-Id", requestId)
            .build();
        
        ServerWebExchange modifiedExchange = exchange.mutate()
            .request(modifiedRequest)
            .build();
        
        // Log inicial
        logger.info("🔵 [{}] Incoming request: {} {} from {}",
            requestId,
            request.getMethod(),
            request.getPath(),
            request.getRemoteAddress());
        
        // Obtener información del usuario autenticado si existe
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .defaultIfEmpty(new NoAuthentication())
            .flatMap(auth -> {
                if (auth.isAuthenticated() && !(auth instanceof NoAuthentication)) {
                    logger.info("🔐 [{}] Authenticated user: {} with roles: {}",
                        requestId,
                        auth.getName(),
                        auth.getAuthorities());
                } else {
                    logger.info("🔓 [{}] Unauthenticated request", requestId);
                }
                
                return chain.filter(modifiedExchange)
                    .doFinally(signalType -> {
                        long duration = System.currentTimeMillis() - startTime;
                        logger.info("🟢 [{}] Request completed in {}ms with status: {}",
                            requestId,
                            duration,
                            exchange.getResponse().getStatusCode());
                    });
            });
    }

    @Override
    public int getOrder() {
        return -1; // Alta prioridad para ejecutarse primero
    }
    
    /**
     * Clase interna para representar ausencia de autenticación
     */
    private static class NoAuthentication implements Authentication {
        @Override
        public String getName() {
            return "anonymous";
        }
        
        @Override
        public Collection getAuthorities() {
            return List.of();
        }
        
        @Override
        public Object getCredentials() {
            return null;
        }
        
        @Override
        public Object getDetails() {
            return null;
        }
        
        @Override
        public Object getPrincipal() {
            return "anonymous";
        }
        
        @Override
        public boolean isAuthenticated() {
            return false;
        }
        
        @Override
        public void setAuthenticated(boolean isAuthenticated) {
            // No-op
        }
    }
}
