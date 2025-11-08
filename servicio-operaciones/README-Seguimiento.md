# 📍 Funcionalidad de Seguimiento de Envíos (RF#2)

## 🎯 Objetivo

Implementar la funcionalidad de **seguimiento en tiempo real** de contenedores y solicitudes de transporte, permitiendo a los clientes consultar el estado y ubicación de sus envíos.

---

## 📋 Requerimiento Funcional

**RF#2**: Consultar estado del transporte de un contenedor

Los clientes deben poder:
- Consultar el estado actual de un contenedor específico
- Ver la ubicación aproximada del contenedor
- Consultar el progreso completo de una solicitud de transporte
- Ver el tiempo estimado de llegada (ETA)

---

## 🏗️ Arquitectura Implementada

### DTOs Creados

#### 1. **ContenedorEstadoDTO**
```java
- id: Long
- numero: String
- estado: String (EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO)
- ubicacionActual: String (descripción textual)
- nombreCliente: String
- solicitudId: Long
```

#### 2. **SolicitudEstadoDTO**
```java
- id: Long
- numero: String
- estado: String (BORRADOR, PROGRAMADA, EN_TRANSITO, ENTREGADA)
- contenedor: ContenedorEstadoDTO
- rutaActual: RutaDTO
- historialTramos: List<TramoHistorialDTO>
- progreso: double (0-100%)
- etaDestino: String
```

#### 3. **RutaDTO**
```java
- id: Long
- origen: String
- destino: String
- distanciaKm: double
- tiempoEstimadoHoras: int
```

#### 4. **TramoHistorialDTO** (Para futuras implementaciones)
```java
- orden: int
- tipo: String
- estado: String
- puntoInicio: String
- puntoFin: String
- fechaHoraInicio: LocalDateTime
- fechaHoraFin: LocalDateTime
- camion: String
```

---

## 🔧 Servicios Implementados

### **ContenedorService**

#### Método: `consultarEstado(Long id)`
- Busca el contenedor por ID
- Determina la ubicación actual según el estado
- Obtiene información del cliente asociado
- Busca la solicitud relacionada
- Retorna `ContenedorEstadoDTO` con toda la información

**Lógica de Ubicación:**
| Estado | Descripción |
|--------|-------------|
| `EN_ORIGEN` | "El contenedor se encuentra en la dirección de origen, listo para ser recogido" |
| `EN_DEPOSITO` | "El contenedor está almacenado en un depósito intermedio de la ruta" |
| `EN_VIAJE` | "El contenedor está en tránsito hacia el siguiente punto de la ruta" |
| `ENTREGADO` | "El contenedor ha sido entregado exitosamente en la dirección de destino" |

---

### **SolicitudService**

#### Método: `consultarEstadoSolicitud(Long id)`
- Busca la solicitud por ID
- Construye el estado del contenedor asociado
- Obtiene información de la ruta
- Calcula el progreso basado en el estado
- Determina el ETA (Estimated Time of Arrival)
- Retorna `SolicitudEstadoDTO` completo

**Lógica de Progreso:**
| Estado Solicitud | Progreso % |
|-----------------|------------|
| `BORRADOR` | 10% |
| `PROGRAMADA` | 25% |
| `EN_TRANSITO` | 60% |
| `ENTREGADA` | 100% |

**Lógica de ETA:**
| Estado | ETA Mostrado |
|--------|--------------|
| `BORRADOR` | "Pendiente de programación" |
| `PROGRAMADA` | "Esperando inicio de transporte" |
| `EN_TRANSITO` | "Aproximadamente X horas" |
| `ENTREGADA` | "Ya entregado" |

---

## 🌐 Endpoints REST

### 1. Consultar Estado de Contenedor

```
GET /api/contenedores/{id}/estado
```

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "numero": "CONT-TEST-001",
  "estado": "EN_ORIGEN",
  "ubicacionActual": "El contenedor se encuentra en la dirección de origen...",
  "nombreCliente": "Juan Pérez",
  "solicitudId": 1
}
```

**Error (404):** Contenedor no encontrado

---

### 2. Consultar Estado Completo de Solicitud

```
GET /api/solicitudes/{id}/estado
```

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "estado": "BORRADOR",
  "contenedor": { ... },
  "rutaActual": { ... },
  "historialTramos": [],
  "progreso": 10.0,
  "etaDestino": "Pendiente de programación"
}
```

**Error (404):** Solicitud no encontrada

---

## 🧪 Pruebas

### Comandos PowerShell:

```powershell
# 1. Consultar estado de contenedor
Invoke-RestMethod -Uri "http://localhost:8082/api/contenedores/1/estado" -Method GET

# 2. Consultar estado de solicitud
Invoke-RestMethod -Uri "http://localhost:8082/api/solicitudes/1/estado" -Method GET
```

Ver archivo `test-seguimiento.md` para pruebas completas.

---

## 📊 Casos de Uso

### Caso 1: Cliente consulta estado de su envío
1. Cliente accede a portal web
2. Ingresa número de contenedor o solicitud
3. Sistema muestra estado actual y ubicación
4. Cliente ve progreso y ETA estimado

### Caso 2: Notificación proactiva
1. Sistema detecta cambio de estado
2. Se envía notificación al cliente
3. Cliente puede consultar detalles en tiempo real

### Caso 3: Seguimiento en ruta
1. Contenedor está EN_VIAJE
2. Cliente consulta estado
3. Sistema muestra:
   - Progreso: 60%
   - Ubicación: "En tránsito..."
   - ETA: "Aproximadamente 4 horas"

---

## 🔮 Futuras Mejoras

### Fase 2: Historial de Tramos
- [ ] Mostrar todos los tramos de la ruta
- [ ] Indicar tramos completados y pendientes
- [ ] Mostrar camiones asignados a cada tramo
- [ ] Timestamps de inicio/fin de cada tramo

### Fase 3: Ubicación en Tiempo Real
- [ ] Integración con GPS de camiones
- [ ] Mapa interactivo con posición actual
- [ ] Actualización automática cada X minutos

### Fase 4: Notificaciones
- [ ] Webhook cuando cambia el estado
- [ ] Emails automáticos al cliente
- [ ] Alertas de demoras o incidencias

---

## ✅ Estado de Implementación

- ✅ DTOs de seguimiento creados
- ✅ Lógica de negocio en servicios
- ✅ Endpoints REST implementados
- ✅ Compilación exitosa
- ✅ Tests pasando
- ✅ Documentación actualizada
- ✅ Casos de prueba documentados

---

## 📚 Referencias

- **Endpoints-Documentacion.md**: Especificaciones completas de API
- **test-seguimiento.md**: Guía de pruebas paso a paso
- **RF#2**: Requerimiento funcional original del sistema

---

**Fecha de Implementación**: Octubre 2025  
**Versión**: 1.0  
**Autor**: Sistema de Logística de Transporte de Contenedores
