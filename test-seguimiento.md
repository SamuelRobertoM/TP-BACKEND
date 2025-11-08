# 🚀 TEST: Funcionalidad de Seguimiento (RF#2)

## Pruebas de los Endpoints de Seguimiento

### 1️⃣ Consultar Estado de Contenedor
```powershell
# GET /api/contenedores/{id}/estado
Invoke-RestMethod -Uri "http://localhost:8082/api/contenedores/1/estado" -Method GET | ConvertTo-Json -Depth 5
```

**Respuesta Esperada:**
```json
{
  "id": 1,
  "numero": "CONT-TEST-001",
  "estado": "EN_ORIGEN",
  "ubicacionActual": "El contenedor se encuentra en la dirección de origen, listo para ser recogido.",
  "nombreCliente": "Juan Pérez",
  "solicitudId": 1
}
```

---

### 2️⃣ Consultar Estado Completo de Solicitud
```powershell
# GET /api/solicitudes/{id}/estado
Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes/1/estado" -Method GET | ConvertTo-Json -Depth 10
```

**Respuesta Esperada:**
```json
{
  "id": 1,
  "numero": null,
  "estado": "BORRADOR",
  "contenedor": {
    "id": 1,
    "numero": "CONT-TEST-001",
    "estado": "EN_ORIGEN",
    "ubicacionActual": "El contenedor se encuentra en la dirección de origen, listo para ser recogido.",
    "nombreCliente": "Juan Pérez",
    "solicitudId": 1
  },
  "rutaActual": {
    "id": 1,
    "origen": "Puerto de Buenos Aires, Terminal 4",
    "destino": "Depósito Central Rosario",
    "distanciaKm": 281.31,
    "tiempoEstimadoHoras": 4
  },
  "historialTramos": [],
  "progreso": 10.0,
  "etaDestino": "Pendiente de programación"
}
```

---

## 🎯 Estados del Contenedor

| Estado | Descripción | Ubicación Mostrada |
|--------|-------------|-------------------|
| `EN_ORIGEN` | Esperando recolección | "En la dirección de origen, listo para ser recogido" |
| `EN_DEPOSITO` | En depósito intermedio | "Almacenado en un depósito intermedio" |
| `EN_VIAJE` | En tránsito | "En tránsito hacia el siguiente punto" |
| `ENTREGADO` | Completado | "Entregado exitosamente en destino" |

---

## 📊 Progreso de la Solicitud

| Estado Solicitud | Progreso % | ETA |
|-----------------|------------|-----|
| `BORRADOR` | 10% | "Pendiente de programación" |
| `PROGRAMADA` | 25% | "Esperando inicio de transporte" |
| `EN_TRANSITO` | 60% | "Aproximadamente X horas" |
| `ENTREGADA` | 100% | "Ya entregado" |

---

## 🧪 Caso de Prueba Completo

### Paso 1: Crear una solicitud
```powershell
$body = Get-Content test-solicitud.json -Raw
$response = Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes" -Method POST -Body $body -ContentType "application/json"
$solicitudId = $response.id
$contenedorId = $response.contenedor.id
```

### Paso 2: Consultar estado del contenedor
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/contenedores/$contenedorId/estado" -Method GET | ConvertTo-Json -Depth 5
```

### Paso 3: Consultar estado completo de la solicitud
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes/$solicitudId/estado" -Method GET | ConvertTo-Json -Depth 10
```

---

## ✅ Verificaciones

- [x] Endpoint GET /api/contenedores/{id}/estado funciona
- [x] Endpoint GET /api/solicitudes/{id}/estado funciona
- [x] Estado del contenedor se muestra correctamente
- [x] Ubicación actual se determina según el estado
- [x] Progreso se calcula correctamente
- [x] ETA se muestra según el estado
- [x] Información del cliente se incluye
- [x] Información de la ruta se incluye
- [x] Retorna 404 si no existe el recurso
