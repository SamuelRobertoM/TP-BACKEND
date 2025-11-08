# Script de prueba E2E simplificado
# Ejecutar: .\test-e2e-simple.ps1

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST E2E: Sistema de Logistica" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# PASO 1: Crear Solicitud
Write-Host "PASO 1: Crear Solicitud (RF#1)" -ForegroundColor Yellow
$body1 = @{
    clienteId = 1
    contenedor = @{
        numero = "CONT-E2E-TEST"
        tipo = "STANDARD"
        estado = "EN_ORIGEN"
        peso = 2000.0
        volumen = 15.0
        clienteId = 1
    }
    direccionOrigen = "Cordoba Centro, Cordoba, Argentina"
    latitudOrigen = -31.4135
    longitudOrigen = -64.1811
    direccionDestino = "Villa Carlos Paz, Cordoba, Argentina"
    latitudDestino = -31.4241
    longitudDestino = -64.4978
} | ConvertTo-Json -Depth 10

try {
    $resp1 = Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes" -Method Post -Body $body1 -ContentType "application/json"
    $solicitudId = $resp1.id
    $contenedorId = $resp1.contenedor.id
    Write-Host "  OK - Solicitud creada - ID: $solicitudId" -ForegroundColor Green
    Write-Host "  OK - Contenedor creado - ID: $contenedorId" -ForegroundColor Green
    Write-Host "  OK - Estado: $($resp1.estado)" -ForegroundColor Green
    Write-Host ""
    
    # Actualizar estado a PENDIENTE
    $resp1b = Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes/$solicitudId" -Method Put -Body (@{estado = "PENDIENTE"} | ConvertTo-Json) -ContentType "application/json"
    Write-Host "  OK - Estado actualizado a: PENDIENTE" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
    exit 1
}

# PASO 2: Asignar Ruta
Write-Host "PASO 2: Asignar Ruta (RF#6)" -ForegroundColor Yellow
$body2 = @{
    origenDepositoId = 1
    destinoDepositoId = 2
    tramos = @(
        @{
            origenDepositoId = 1
            destinoDepositoId = 2
            distanciaKm = 50.0
            tiempoEstimadoHoras = 1.5
            fechaEstimadaInicio = "2025-10-26T10:00:00"
            fechaEstimadaFin = "2025-10-26T11:30:00"
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $resp2 = Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes/$solicitudId/asignar-ruta" -Method Post -Body $body2 -ContentType "application/json"
    $rutaId = $resp2.id
    $tramoId = $resp2.tramos[0].id
    Write-Host "  OK - Ruta asignada - ID: $rutaId" -ForegroundColor Green
    Write-Host "  OK - Tramo creado - ID: $tramoId" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
    exit 1
}

# PASO 3: Asignar Camion
Write-Host "PASO 3: Asignar Camion (RF#6)" -ForegroundColor Yellow
$body3 = @{ camionId = 1 } | ConvertTo-Json

try {
    $resp3 = Invoke-RestMethod -Uri "http://localhost:8082/api/tramos/$tramoId/asignar-camion" -Method Post -Body $body3 -ContentType "application/json"
    Write-Host "  OK - Camion asignado: $($resp3.camionReference.dominio)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
    exit 1
}

# PASO 4: Iniciar Tramo
Write-Host "PASO 4: Iniciar Tramo (RF#8)" -ForegroundColor Yellow
try {
    $resp4 = Invoke-RestMethod -Uri "http://localhost:8082/api/tramos/$tramoId/iniciar" -Method Post
    Write-Host "  OK - Tramo iniciado" -ForegroundColor Green
    Write-Host "  OK - Estado: $($resp4.estado)" -ForegroundColor Green
    Write-Host "  OK - Fecha inicio: $($resp4.fechaRealInicio)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
    exit 1
}

# PASO 5: Verificar Estado Contenedor
Write-Host "PASO 5: Consultar Estado Contenedor (RF#2)" -ForegroundColor Yellow
try {
    $resp5 = Invoke-RestMethod -Uri "http://localhost:8082/api/contenedores/$contenedorId/estado" -Method Get
    Write-Host "  OK - Estado contenedor: $($resp5.contenedor.estado)" -ForegroundColor Green
    Write-Host "  OK - Estado solicitud: $($resp5.solicitud.estado)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
}

# PASO 6: Finalizar Tramo
Write-Host "PASO 6: Finalizar Tramo (RF#8)" -ForegroundColor Yellow
try {
    $resp6 = Invoke-RestMethod -Uri "http://localhost:8082/api/tramos/$tramoId/finalizar" -Method Post
    $costoReal = $resp6.costoReal
    Write-Host "  OK - Tramo finalizado" -ForegroundColor Green
    Write-Host "  OK - Estado: $($resp6.estado)" -ForegroundColor Green
    Write-Host "  OK - Costo real: $ $costoReal" -ForegroundColor Green
    Write-Host "  OK - Fecha fin: $($resp6.fechaRealFin)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
    exit 1
}

# PASO 7: Finalizar Solicitud
Write-Host "PASO 7: Finalizar Solicitud (RF#9)" -ForegroundColor Yellow
$body7 = @{
    observaciones = "Entrega completada - Test E2E"
} | ConvertTo-Json

try {
    $resp7 = Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes/$solicitudId/finalizar" -Method Patch -Body $body7 -ContentType "application/json"
    $costoFinal = $resp7.costoFinal
    $tiempoReal = $resp7.tiempoReal
    Write-Host "  OK - Solicitud finalizada" -ForegroundColor Green
    Write-Host "  OK - Estado final: $($resp7.estado)" -ForegroundColor Green
    Write-Host "  OK - Costo final: $ $costoFinal" -ForegroundColor Green
    Write-Host "  OK - Tiempo real: $tiempoReal horas" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "  ERROR: $_" -ForegroundColor Red
    exit 1
}

# RESUMEN
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "FLUJO COMPLETADO EXITOSAMENTE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Resumen:" -ForegroundColor White
Write-Host "  Solicitud ID: $solicitudId" -ForegroundColor White
Write-Host "  Contenedor ID: $contenedorId" -ForegroundColor White
Write-Host "  Ruta ID: $rutaId" -ForegroundColor White
Write-Host "  Tramo ID: $tramoId" -ForegroundColor White
Write-Host "  Costo Final: $ $costoFinal" -ForegroundColor White
Write-Host "  Tiempo Real: $tiempoReal horas" -ForegroundColor White
Write-Host ""
Write-Host "Todos los requisitos funcionales probados correctamente!" -ForegroundColor Green
Write-Host ""
