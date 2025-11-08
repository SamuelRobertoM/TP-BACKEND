# 💰 RF#8 - Cálculo del Costo Real de Tramos

## 📋 Descripción

Esta funcionalidad calcula automáticamente el **costo real** de un tramo cuando el transportista lo finaliza. El cálculo utiliza datos actualizados obtenidos desde **servicio-flota** mediante comunicación REST.

---

## 🏗️ Arquitectura

```
servicio-operaciones (8082)
    │
    ├─ TramoController
    │   └─ POST /api/tramos/{id}/finalizar
    │       │
    ├─ TramoService
    │   └─ finalizarTramo(Long tramoId)
    │       │
    │       ├─ 1️⃣ Validar estado del tramo (debe estar "INICIADO")
    │       │
    │       ├─ 2️⃣ FlotaServiceClient.obtenerTarifaActiva()
    │       │    └─ GET http://localhost:8081/api/tarifas/actual
    │       │         └─ TarifaDTO { cargoGestionPorTramo, precioLitroCombustible }
    │       │
    │       ├─ 3️⃣ FlotaServiceClient.obtenerCamionPorId(camionId)
    │       │    └─ GET http://localhost:8081/api/camiones/{id}
    │       │         └─ CamionDTO { consumoCombustiblePorKm, costoPorKm }
    │       │
    │       ├─ 4️⃣ CALCULAR COSTO REAL
    │       │    costoReal = cargoGestion 
    │       │               + (costoPorKm × distanciaKm) 
    │       │               + (consumoCombustible × distanciaKm × precioCombustible)
    │       │
    │       ├─ 5️⃣ Actualizar Tramo
    │       │    - estado = "FINALIZADO"
    │       │    - fechaRealFin = LocalDateTime.now()
    │       │    - costoReal = (valor calculado)
    │       │
    │       ├─ 6️⃣ Liberar Camión
    │       │    - camion.disponible = true
    │       │
    │       └─ 7️⃣ Actualizar Contenedor/Solicitud (si es último tramo)
    │            - contenedor.estado = "ENTREGADO"
    │            - solicitud.estado = "ENTREGADA"
```

---

## 📐 Fórmula de Cálculo

```java
// Componentes del costo
double cargoGestion = tarifa.getCargoGestionPorTramo();      // Costo fijo por tramo
double costoPorKm = camion.getCostoPorKm() * tramo.getDistanciaKm();  // Costo variable por km
double costoCombustible = camion.getConsumoCombustiblePorKm() 
                         * tramo.getDistanciaKm() 
                         * tarifa.getPrecioLitroCombustible();  // Costo de combustible

// Costo total
double costoReal = cargoGestion + costoPorKm + costoCombustible;
```

### Ejemplo Numérico

**Datos:**
- Tarifa:
  - `cargoGestionPorTramo = 500.00` (pesos)
  - `precioLitroCombustible = 150.00` (pesos/litro)
- Camión:
  - `costoPorKm = 10.00` (pesos/km)
  - `consumoCombustiblePorKm = 0.35` (litros/km)
- Tramo:
  - `distanciaKm = 120.5` (km)

**Cálculo:**
```
cargoGestion = 500.00
costoPorKm = 10.00 × 120.5 = 1,205.00
costoCombustible = 0.35 × 120.5 × 150.00 = 6,326.25

costoReal = 500.00 + 1,205.00 + 6,326.25 = 8,031.25 pesos
```

---

## 🔄 Flujo Completo

### Prerequisitos
1. ✅ Servicio-flota debe estar corriendo en `http://localhost:8081`
2. ✅ Debe existir una **tarifa activa** en servicio-flota
3. ✅ El tramo debe tener un **camión asignado**
4. ✅ El tramo debe estar en estado **"INICIADO"**

### Pasos

#### 1️⃣ Iniciar Tramo
```http
POST http://localhost:8082/api/tramos/1/iniciar
```

**Response:**
```json
{
  "id": 1,
  "estado": "INICIADO",
  "fechaRealInicio": "2025-01-26T02:45:00",
  "camion": {
    "id": 1,
    "dominio": "AA123BB"
  }
}
```

#### 2️⃣ Finalizar Tramo (RF#8)
```http
POST http://localhost:8082/api/tramos/1/finalizar
```

**Response:**
```json
{
  "id": 1,
  "estado": "FINALIZADO",
  "fechaRealInicio": "2025-01-26T02:45:00",
  "fechaRealFin": "2025-01-26T05:30:00",
  "distanciaKm": 120.5,
  "costoAproximado": 7500.0,
  "costoReal": 8031.25,   // ← CALCULADO AUTOMÁTICAMENTE
  "camion": {
    "id": 1,
    "dominio": "AA123BB",
    "disponible": true    // ← Liberado automáticamente
  }
}
```

---

## 📊 Logs Detallados

Durante la ejecución de `finalizarTramo()`, el sistema genera logs informativos:

```log
INFO  TramoService : Calculando costo real para tramo ID: 1
DEBUG TramoService : Tarifa obtenida: cargoGestion=500.0, precioCombustible=150.0
DEBUG TramoService : Camión obtenido: consumo=0.35 L/km, costoPorKm=10.0
INFO  TramoService : Costo real calculado para tramo 1: cargo=500.0, costoPorKm=1205.0, combustible=6326.25, TOTAL=8031.25
INFO  TramoService : Ruta completada. Solicitud 1 marcada como ENTREGADA
DEBUG TramoService : Camión AA123BB liberado y marcado como disponible
```

---

## ⚠️ Manejo de Errores

### Error 1: Tramo no está en estado INICIADO
```json
{
  "timestamp": "2025-01-26T05:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El tramo no está en estado 'INICIADO'. Estado actual: PENDIENTE"
}
```

### Error 2: No hay tarifa activa
```json
{
  "timestamp": "2025-01-26T05:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "No se pudo obtener la tarifa activa desde servicio-flota"
}
```

### Error 3: Camión no encontrado en servicio-flota
```json
{
  "timestamp": "2025-01-26T05:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "No se pudo obtener los datos del camión ID: 999"
}
```

### Error 4: Servicio-flota no disponible
```log
ERROR FlotaServiceClient : Error al obtener tarifa activa: Connection refused
WARN  FlotaServiceClient : No se pudo conectar con servicio-flota en http://localhost:8081
```

---

## 🧪 Testing

### Test Manual

#### Setup
```bash
# Terminal 1 - Iniciar servicio-flota
cd servicio-flota
mvnw spring-boot:run

# Terminal 2 - Iniciar servicio-operaciones
cd servicio-operaciones
mvnw spring-boot:run
```

#### Crear Tarifa Activa (en servicio-flota)
```http
POST http://localhost:8081/api/tarifas
Content-Type: application/json

{
  "costoKmBase": 5.0,
  "precioLitroCombustible": 150.0,
  "cargoGestionPorTramo": 500.0,
  "vigenciaDesde": "2025-01-01T00:00:00",
  "vigenciaHasta": "2025-12-31T23:59:59",
  "activa": true
}
```

#### Crear Camión (en servicio-flota)
```http
POST http://localhost:8081/api/camiones
Content-Type: application/json

{
  "dominio": "AA123BB",
  "nombreTransportista": "Juan Pérez",
  "telefono": "+5493512345678",
  "capacidadPeso": 10000.0,
  "capacidadVolumen": 50.0,
  "consumoCombustiblePorKm": 0.35,
  "costoPorKm": 10.0,
  "disponible": true
}
```

#### Ejecutar Flujo Completo
```bash
# 1. Crear solicitud con contenedor
POST http://localhost:8082/api/solicitudes

# 2. Obtener rutas tentativas
GET http://localhost:8082/api/solicitudes/1/rutas/tentativas

# 3. Asignar ruta (crea tramos)
POST http://localhost:8082/api/solicitudes/1/asignar-ruta

# 4. Asignar camión al tramo
POST http://localhost:8082/api/tramos/1/asignar-camion
{ "camionId": 1 }

# 5. Iniciar tramo
POST http://localhost:8082/api/tramos/1/iniciar

# 6. Finalizar tramo (RF#8 - calcula costo real)
POST http://localhost:8082/api/tramos/1/finalizar

# 7. Verificar costo calculado
GET http://localhost:8082/api/tramos/1
```

---

## 📁 Archivos Modificados

### `servicio-operaciones/src/main/java/.../services/TramoService.java`
- ✅ Inyección de `FlotaServiceClient`
- ✅ Método `finalizarTramo()` actualizado con cálculo de costo real
- ✅ Logs informativos (DEBUG, INFO)
- ✅ Manejo robusto de errores

### `servicio-operaciones/Endpoints-Documentacion.md`
- ✅ Actualizada sección de Tramos
- ✅ Documentado RF#8 con fórmula y detalles de implementación

---

## 🔗 Referencias

- **FlotaServiceClient**: Ver `FLOTA_CLIENT_README.md`
- **DTOs de Flota**: `dtos/flota/TarifaDTO.java`, `dtos/flota/CamionDTO.java`
- **Configuración REST**: `config/RestClientConfig.java`
- **Endpoint de Prueba**: `controllers/FlotaTestController.java`

---

## ✅ Estado: Implementado

- [x] Comunicación con servicio-flota
- [x] Obtención de tarifa activa
- [x] Obtención de datos del camión
- [x] Cálculo de costo real con fórmula correcta
- [x] Actualización del campo `costoReal` en base de datos
- [x] Logs informativos
- [x] Manejo de errores
- [x] Documentación completa
- [x] Compilación exitosa

---

**Fecha de implementación**: 26 de Enero 2025  
**Desarrollador**: Equipo Backend TPI
