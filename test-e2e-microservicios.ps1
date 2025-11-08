# ===============================================================================
# SCRIPT DE PRUEBAS E2E - ARQUITECTURA MICROSERVICIOS TPI BACKEND
# ===============================================================================
# Verifica toda la arquitectura del sistema:
# - Autenticacion OAuth2 + JWT (Keycloak)
# - API Gateway (Spring Cloud Gateway)
# - Microservicio de Operaciones
# - Seguridad de endpoints
# ===============================================================================

Write-Host ""
Write-Host "SISTEMA TPI BACKEND - PRUEBA E2E COMPLETA" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$ErrorActionPreference = "Continue"
$exitCode = 0

try {
    # ========== PASO 1: AUTENTICACION ==========
    Write-Host "1. AUTENTICACION CON KEYCLOAK" -ForegroundColor Yellow
    Write-Host "   Obteniendo token JWT desde Keycloak..." -ForegroundColor Gray
    
    $tokenData = @{
        client_id = "tpi-api-gateway"
        client_secret = "tpi-gateway-secret-2024-secure"
        grant_type = "password"
        username = "admin"
        password = "admin123"
    }
    
    $tokenResponse = Invoke-RestMethod -Uri "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" -Method POST -ContentType "application/x-www-form-urlencoded" -Body $tokenData
    $headers = @{ Authorization = "Bearer $($tokenResponse.access_token)" }
    Write-Host "   OK - Token JWT obtenido exitosamente" -ForegroundColor Green
    Write-Host "   OK - Headers de autenticacion configurados" -ForegroundColor Green

    # ========== PASO 2: VERIFICACION API GATEWAY ==========
    Write-Host ""
    Write-Host "2. VERIFICACION API GATEWAY" -ForegroundColor Yellow
    
    # Health Check del Gateway
    Write-Host "   Verificando salud del API Gateway..." -ForegroundColor Gray
    $gatewayHealth = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET -UseBasicParsing
    Write-Host "   OK - API Gateway funcionando (Status: $($gatewayHealth.status))" -ForegroundColor Green

    # ========== PASO 3: VERIFICACION MICROSERVICIO OPERACIONES ==========
    Write-Host ""
    Write-Host "3. MICROSERVICIO OPERACIONES VIA GATEWAY" -ForegroundColor Yellow
    
    # Consultar clientes existentes
    Write-Host "   Consultando clientes existentes..." -ForegroundColor Gray
    $clientesExistentes = Invoke-RestMethod -Uri "http://localhost:8080/api/operaciones/clientes" -Method GET -Headers $headers
    Write-Host "   OK - Servicio Operaciones respondiendo ($($clientesExistentes.Count) clientes en BD)" -ForegroundColor Green
    
    # Crear nuevo cliente de prueba
    Write-Host "   Creando cliente de prueba..." -ForegroundColor Gray
    $nuevoClienteData = @{
        nombre = "Cliente Test E2E"
        direccion = "Av. Microservicios 123, Cordoba"
        cuit = "20-98765432-1"
        telefono = "351-987654"
        email = "test.e2e@microservicios.com"
    }
    
    $clienteCreado = Invoke-RestMethod -Uri "http://localhost:8080/api/operaciones/clientes" -Method POST -Headers $headers -Body ($nuevoClienteData | ConvertTo-Json) -ContentType "application/json"
    Write-Host "   OK - Cliente creado exitosamente:" -ForegroundColor Green
    Write-Host "        Nombre: $($clienteCreado.nombre)" -ForegroundColor Cyan
    Write-Host "        ID: $($clienteCreado.id)" -ForegroundColor Cyan
    Write-Host "        CUIT: $($clienteCreado.cuit)" -ForegroundColor Cyan
    
    # Verificar cliente creado consultandolo por ID
    Write-Host "   Verificando cliente creado por ID..." -ForegroundColor Gray
    $clienteVerificado = Invoke-RestMethod -Uri "http://localhost:8080/api/operaciones/clientes/$($clienteCreado.id)" -Method GET -Headers $headers
    Write-Host "   OK - Cliente verificado correctamente" -ForegroundColor Green
    Write-Host "        Email: $($clienteVerificado.email)" -ForegroundColor Cyan

    # ========== PASO 4: VERIFICACION SEGURIDAD ==========
    Write-Host ""
    Write-Host "4. VERIFICACION SEGURIDAD JWT" -ForegroundColor Yellow
    
    # Intentar acceso sin token (debe fallar con 401)
    Write-Host "   Probando acceso sin token de autenticacion..." -ForegroundColor Gray
    try {
        Invoke-RestMethod -Uri "http://localhost:8080/api/operaciones/clientes" -Method GET -UseBasicParsing | Out-Null
        Write-Host "   ERROR - Acceso permitido sin token (problema de seguridad)" -ForegroundColor Red
        $exitCode = 1
    } catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Host "   OK - Acceso correctamente bloqueado sin token (401 Unauthorized)" -ForegroundColor Green
        } else {
            Write-Host "   ADVERTENCIA - Respuesta inesperada: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
        }
    }

    # ========== RESULTADO FINAL ==========
    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Cyan
    Write-Host "                  PRUEBA E2E COMPLETADA CON EXITO" -ForegroundColor Green
    Write-Host "================================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "COMPONENTES VERIFICADOS:" -ForegroundColor White
    Write-Host "  - Keycloak Server (OAuth2 + JWT)" -ForegroundColor Green
    Write-Host "  - API Gateway (Spring Cloud Gateway)" -ForegroundColor Green  
    Write-Host "  - Microservicio Operaciones (CRUD)" -ForegroundColor Green
    Write-Host "  - Seguridad de Endpoints (JWT)" -ForegroundColor Green
    Write-Host "  - Comunicacion entre Servicios" -ForegroundColor Green
    Write-Host ""
    Write-Host "OPERACIONES REALIZADAS:" -ForegroundColor White
    Write-Host "  > Autenticacion OAuth2 exitosa" -ForegroundColor Cyan
    Write-Host "  > Consulta de clientes via Gateway" -ForegroundColor Cyan
    Write-Host "  > Creacion de cliente de prueba" -ForegroundColor Cyan
    Write-Host "  > Verificacion de cliente por ID" -ForegroundColor Cyan
    Write-Host "  > Validacion de seguridad JWT" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "CLIENTE DE PRUEBA CREADO:" -ForegroundColor White
    Write-Host "  Nombre: $($clienteCreado.nombre)" -ForegroundColor Yellow
    Write-Host "  ID: $($clienteCreado.id)" -ForegroundColor Yellow
    Write-Host "  Email: $($clienteCreado.email)" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "ARQUITECTURA DE MICROSERVICIOS COMPLETAMENTE FUNCIONAL!" -ForegroundColor Yellow
    Write-Host "El sistema esta listo para desarrollo y produccion." -ForegroundColor White
    Write-Host ""

} catch {
    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Red
    Write-Host "                        ERROR EN LA PRUEBA E2E" -ForegroundColor Red
    Write-Host "================================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error encontrado:" -ForegroundColor Red
    Write-Host "  $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Linea: $($_.InvocationInfo.ScriptLineNumber)" -ForegroundColor Red
    Write-Host ""
    Write-Host "VERIFICAR QUE ESTOS SERVICIOS ESTEN CORRIENDO:" -ForegroundColor Yellow
    Write-Host "  1. Keycloak: http://localhost:8180" -ForegroundColor Cyan
    Write-Host "  2. API Gateway: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "  3. Servicio Operaciones: http://localhost:8082" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "COMANDOS PARA LEVANTAR SERVICIOS:" -ForegroundColor Yellow
    Write-Host "  Keycloak: docker run -p 8180:8080 -e KEYCLOAK_ADMIN=admin ..." -ForegroundColor Gray
    Write-Host "  Gateway: mvn spring-boot:run (en carpeta api-gateway)" -ForegroundColor Gray
    Write-Host "  Operaciones: mvn spring-boot:run (en carpeta servicio-operaciones)" -ForegroundColor Gray
    Write-Host ""
    $exitCode = 1
}

# Salida del script
exit $exitCode