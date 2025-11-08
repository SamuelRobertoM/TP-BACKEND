# 📡 DOCUMENTACIÓN DE ENDPOINTS

## Sistema de Logística de Transporte de Contenedores

**Versión**: 1.0  
**Fecha**: Octubre 2025  
**Entrega**: Inicial - Diseño de API

---

## 📋 Índice

1. [Arquitectura General](#arquitectura-general)
2. [Microservicio: Servicio-Flota](#microservicio-servicio-flota)
3. [Microservicio: Servicio-Operaciones](#microservicio-servicio-operaciones)
4. [API Gateway](#api-gateway)
5. [Códigos HTTP Utilizados](#códigos-http-utilizados)
6. [Mapeo de Requerimientos Funcionales](#mapeo-de-requerimientos-funcionales)

---

## 🏗️ Arquitectura General

```
Cliente/Transportista/Operador
         ↓
    API Gateway (Puerto 8080) ← [Keycloak - Autenticación JWT]
         ↓
    ┌────────────────────────────────┐
    ↓                                ↓
Servicio-Flota                 Servicio-Operaciones
(Puerto 8081)                  (Puerto 8082)
    ↓                                ↓
DB Flota (PostgreSQL)          DB Operaciones (PostgreSQL)
                                     ↓
                            Google Maps API
```

---

## 🚛 Microservicio: Servicio-Flota

**Puerto**: 8081  
**Base Path**: `/api`  
**Responsabilidad**: Gestión de recursos físicos (Camiones, Depósitos, Tarifas)

### 📦 Recurso: Camiones

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/camiones` | Operador | Listar todos los camiones | - | `List<CamionDTO>` | 200 | ✅ Implementado |
| **GET** | `/camiones/{id}` | Operador | Obtener camión por ID | `id: Long` (PathVariable) | `CamionDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/camiones/disponibles` | Operador | Listar camiones disponibles con filtros | Query: `pesoMinimo: double`, `volumenMinimo: double` | `List<CamionDTO>` | 200 | ✅ Implementado |
| **POST** | `/camiones` | Operador | Registrar nuevo camión | `CamionCreateDTO` (body) | `CamionDTO` | 201, 400 | ✅ Implementado |
| **PUT** | `/camiones/{id}` | Operador | Actualizar datos de camión | `id: Long`, `CamionUpdateDTO` (body) | `CamionDTO` | 200, 404, 400 | ✅ Implementado |
| **PATCH** | `/camiones/{id}/disponibilidad` | Sistema | Cambiar disponibilidad del camión | `id: Long`, `DisponibilidadDTO` (body) | `CamionDTO` | 200, 404 | 🟡 Pendiente (Lógica) |
| **DELETE** | `/camiones/{id}` | Operador | Eliminar camión | `id: Long` | - | 204, 404 | ✅ Implementado |

#### DTOs - Camiones *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de capa de transferencia de datos

```java
// Entrada
CamionCreateDTO {
    String dominio;                    // REQUERIDO - Patente única
    String nombreTransportista;        // REQUERIDO
    String telefono;                   // REQUERIDO
    double capacidadPeso;              // REQUERIDO - en kg
    double capacidadVolumen;           // REQUERIDO - en m³
    double consumoCombustiblePorKm;    // REQUERIDO - litros/km
    double costoPorKm;                 // REQUERIDO - $/km
}

CamionUpdateDTO {
    String nombreTransportista;        // OPCIONAL
    String telefono;                   // OPCIONAL
    double consumoCombustiblePorKm;    // OPCIONAL
    double costoPorKm;                 // OPCIONAL
}

DisponibilidadDTO {
    boolean disponible;                // true = libre, false = ocupado
}

// Salida
CamionDTO {
    Long id;
    String dominio;
    String nombreTransportista;
    String telefono;
    double capacidadPeso;
    double capacidadVolumen;
    boolean disponible;
    double consumoCombustiblePorKm;
    double costoPorKm;
}
```

---

### 🏢 Recurso: Depósitos

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/depositos` | Operador | Listar todos los depósitos | - | `List<DepositoDTO>` | 200 | ✅ Implementado |
| **GET** | `/depositos/{id}` | Operador | Obtener depósito por ID | `id: Long` | `DepositoDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/depositos` | Operador | Registrar nuevo depósito | `DepositoCreateDTO` (body) | `DepositoDTO` | 201, 400 | ✅ Implementado |
| **PUT** | `/depositos/{id}` | Operador | Actualizar datos de depósito | `id: Long`, `DepositoUpdateDTO` (body) | `DepositoDTO` | 200, 404, 400 | ✅ Implementado |
| **DELETE** | `/depositos/{id}` | Operador | Eliminar depósito | `id: Long` | - | 204, 404 | ✅ Implementado |

#### DTOs - Depósitos *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de capa de transferencia de datos

```java
// Entrada
DepositoCreateDTO {
    String nombre;            // REQUERIDO
    String direccion;         // REQUERIDO
    double latitud;           // REQUERIDO - formato: -90 a 90
    double longitud;          // REQUERIDO - formato: -180 a 180
    double costoEstadiaDiaria; // REQUERIDO - $/día
}

DepositoUpdateDTO {
    String nombre;            // OPCIONAL
    String direccion;         // OPCIONAL
    double costoEstadiaDiaria; // OPCIONAL
}

// Salida
DepositoDTO {
    Long id;
    String nombre;
    String direccion;
    double latitud;
    double longitud;
    double costoEstadiaDiaria;
}
```

---

### 💰 Recurso: Tarifas

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/tarifas/actual` | Todos | Obtener tarifa activa vigente | - | `TarifaDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/tarifas` | Operador | Listar todas las tarifas (históricas) | - | `List<TarifaDTO>` | 200 | ✅ Implementado |
| **GET** | `/tarifas/{id}` | Operador | Obtener tarifa por ID | `id: Long` | `TarifaDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/tarifas` | Operador | Crear nueva tarifa | `TarifaCreateDTO` (body) | `TarifaDTO` | 201, 400 | ✅ Implementado |
| **PUT** | `/tarifas/{id}` | Operador | Actualizar tarifa | `id: Long`, `TarifaUpdateDTO` (body) | `TarifaDTO` | 200, 404, 400 | ✅ Implementado |
| **DELETE** | `/tarifas/{id}` | Operador | Eliminar tarifa (solo si no está activa) | `id: Long` | - | 204, 404, 400 | ✅ Implementado |

**✅ IMPLEMENTADO**: Recurso completo con arquitectura de 4 capas, DTOs, validaciones y lógica de negocio para gestión de tarifas activas.

#### DTOs - Tarifas *(✅ Implementado)*

**Estado de DTOs**: ✅ Implementado - DTOs completos con validaciones Jakarta Validation

```java
// Entrada
TarifaCreateDTO {
    double costoKmBase;             // REQUERIDO - Costo base por km
    double precioLitroCombustible;  // REQUERIDO - Precio actual del litro
    double cargoGestionPorTramo;    // REQUERIDO - Cargo fijo por tramo
    LocalDateTime vigenciaDesde;    // REQUERIDO - Fecha inicio vigencia
}

TarifaUpdateDTO {
    double precioLitroCombustible;  // OPCIONAL
    double cargoGestionPorTramo;    // OPCIONAL
    LocalDateTime vigenciaHasta;    // OPCIONAL - Para cerrar vigencia
    boolean activa;                 // OPCIONAL
}

// Salida
TarifaDTO {
    Long id;
    double costoKmBase;
    double precioLitroCombustible;
    double cargoGestionPorTramo;
    LocalDateTime vigenciaDesde;
    LocalDateTime vigenciaHasta;
    boolean activa;
}
```

---

## 🚀 Microservicio: Servicio-Operaciones

**Puerto**: 8082  
**Base Path**: `/api`  
**Responsabilidad**: Gestión de operaciones de negocio (Clientes, Solicitudes, Rutas, Tramos)

### 👤 Recurso: Clientes

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/clientes` | Operador | Listar todos los clientes | - | `List<ClienteDTO>` | 200 | ✅ Implementado |
| **GET** | `/clientes/{id}` | Operador | Obtener cliente por ID | `id: Long` | `ClienteDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/clientes` | Cliente, Operador | Registrar nuevo cliente | `ClienteCreateDTO` (body) | `ClienteDTO` | 201, 400, 409 | ✅ Implementado |
| **PUT** | `/clientes/{id}` | Cliente, Operador | Actualizar datos de cliente | `id: Long`, `ClienteUpdateDTO` (body) | `ClienteDTO` | 200, 404, 400 | ✅ Implementado |

#### DTOs - Clientes *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de capa de transferencia de datos

```java
// Entrada
ClienteCreateDTO {
    String nombre;     // REQUERIDO
    String email;      // REQUERIDO - único
    String telefono;   // REQUERIDO
    String direccion;  // REQUERIDO
    String cuit;       // REQUERIDO - único
}

ClienteUpdateDTO {
    String nombre;     // OPCIONAL
    String telefono;   // OPCIONAL
    String direccion;  // OPCIONAL
}

// Salida
ClienteDTO {
    Long id;
    String nombre;
    String email;
    String telefono;
    String direccion;
    String cuit;
}
```

---

### 📦 Recurso: Contenedores

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/contenedores` | Operador | Listar contenedores con filtros | Query: `estado`, `clienteId` | `List<ContenedorDTO>` | 200 | ✅ Implementado |
| **GET** | `/contenedores/{id}` | Cliente, Operador | Obtener contenedor por ID | `id: Long` | `ContenedorDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/contenedores/{id}/estado` | Cliente | **[RF#2]** Consultar estado de contenedor (seguimiento) | `id: Long` | `ContenedorEstadoDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/contenedores/pendientes` | Operador | **[RF#5]** Consultar contenedores pendientes de asignación | - | `List<ContenedorPendienteDTO>` | 200 | ✅ Implementado |

#### DTOs - Contenedores *(✅ Implementado)*

**Estado de DTOs**: ✅ Implementado - DTOs completos con lógica de negocio funcionando

```java
// Salida
ContenedorDTO {
    Long id;
    String numero;
    String tipo;
    double peso;
    double volumen;
    String estado;
    String direccionOrigen;
    double latitudOrigen;
    double longitudOrigen;
    String direccionDestino;
    double latitudDestino;
    double longitudDestino;
    ClienteDTO cliente;
}

ContenedorEstadoDTO {
    Long id;
    String numero;
    String estado;                    // EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO
    String ubicacionActual;           // Descripción textual
    TramoDTO tramoActual;             // Tramo en curso (si aplica)
    DepositoDTO depositoActual;       // Depósito actual (si aplica)
}

ContenedorPendienteDTO {
    Long id;
    String numero;
    String estado;                    // EN_ORIGEN, EN_DEPOSITO
    String ubicacionActual;           // Descripción generada según estado
    String cliente;                   // Nombre del cliente
    Long solicitudId;                 // ID de solicitud asociada
}
```

---

### 📋 Recurso: Solicitudes

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **POST** | `/solicitudes` | Cliente | **[RF#1]** Registrar nueva solicitud de transporte | `SolicitudCreateDTO` (body) | `SolicitudDTO` | 201, 400 | ✅ Implementado |
| **GET** | `/solicitudes` | Operador | Listar todas las solicitudes | Query: `estado`, `clienteId` | `List<SolicitudDTO>` | 200 | ✅ Implementado |
| **GET** | `/solicitudes/{id}` | Cliente, Operador | Obtener solicitud por ID | `id: Long` | `SolicitudDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/solicitudes/{id}/estado` | Cliente | **[RF#2]** Consultar estado del transporte | `id: Long` | `SolicitudEstadoDTO` | 200, 404 | ✅ Implementado |
| **PUT** | `/solicitudes/{id}/estado` | Operador, Sistema | Actualizar estado de solicitud | `id: Long`, `EstadoUpdateDTO` (body) | `SolicitudDTO` | 200, 404, 400 | 🟡 Pendiente (Lógica) |
| **PATCH** | `/solicitudes/{id}/finalizar` | Sistema | **[RF#9]** Registrar costos y tiempos finales | `id: Long`, `FinalizacionDTO` (body) | `SolicitudDTO` | 200, 404, 400 | ✅ **Implementado** |

#### DTOs - Solicitudes *(🟡 Pendiente - Lógica)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de DTOs complejos con relaciones y seguimiento

```java
// Entrada
SolicitudCreateDTO {
    ContenedorCreateDTO contenedor;   // REQUERIDO - Se crea el contenedor
    ClienteCreateDTO cliente;         // OPCIONAL - Si es cliente nuevo
    Long clienteId;                   // OPCIONAL - Si cliente ya existe
    String observaciones;             // OPCIONAL
}

ContenedorCreateDTO {
    String numero;                    // REQUERIDO - único
    String tipo;                      // REQUERIDO - STANDARD, REFRIGERADO, etc.
    double peso;                      // REQUERIDO - en kg
    double volumen;                   // REQUERIDO - en m³
    String direccionOrigen;           // REQUERIDO
    double latitudOrigen;             // REQUERIDO
    double longitudOrigen;            // REQUERIDO
    String direccionDestino;          // REQUERIDO
    double latitudDestino;            // REQUERIDO
    double longitudDestino;           // REQUERIDO
}

EstadoUpdateDTO {
    String estado;                    // BORRADOR, PROGRAMADA, EN_TRANSITO, ENTREGADA
}

FinalizacionDTO {
    double costoFinal;                // REQUERIDO
    double tiempoReal;                // REQUERIDO - en horas
}

// Salida
SolicitudDTO {
    Long id;
    String numero;
    LocalDateTime fechaSolicitud;
    String estado;
    String observaciones;
    ClienteDTO cliente;
    ContenedorDTO contenedor;
    RutaDTO ruta;
    double costoEstimado;
    double tiempoEstimado;
    double costoFinal;
    double tiempoReal;
}

SolicitudEstadoDTO {
    Long id;
    String numero;
    String estado;
    ContenedorEstadoDTO contenedor;
    RutaDTO rutaActual;
    List<TramoHistorialDTO> historialTramos;  // Estados cronológicos
    double progreso;                           // Porcentaje 0-100
}

TramoHistorialDTO {
    int orden;
    String tipo;
    String estado;
    String puntoInicio;
    String puntoFin;
    LocalDateTime fechaHoraInicio;
    LocalDateTime fechaHoraFin;
    String camion;
}
```

---

### 🛣️ Recurso: Rutas

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/solicitudes/{id}/rutas/tentativas` | Operador | **[RF#3]** Consultar rutas tentativas con cálculos (Google Maps) | `solicitudId: Long` | `List<RutaTentativaDTO>` | 200, 404 | ✅ Implementado |
| **POST** | `/solicitudes/{solicitudId}/asignar-ruta` | Operador | **[RF#4]** Asignar ruta con tramos a solicitud | `solicitudId: Long`, `RutaCreateDTO` (body) | `RutaDTO` | 201, 400, 404 | ✅ Implementado |
| **GET** | `/rutas/{id}` | Operador | Obtener ruta por ID | `id: Long` | `RutaDTO` | 200, 404 | ✅ Implementado |

#### DTOs - Rutas *(✅ Implementado)*

**Estado de DTOs**: ✅ Implementado - Integración completa con Google Maps Distance Matrix API

```java
// Entrada
RutaCreateDTO {
    List<TramoCreateDTO> tramos;      // REQUERIDO - Lista ordenada de tramos
}

TramoCreateDTO {
    int orden;                        // REQUERIDO - 1, 2, 3...
    String tipo;                      // REQUERIDO
    double latitudInicio;             // REQUERIDO
    double longitudInicio;            // REQUERIDO
    double latitudFin;                // REQUERIDO
    double longitudFin;               // REQUERIDO
    Long depositoOrigenId;            // OPCIONAL
    Long depositoDestinoId;           // OPCIONAL
    LocalDateTime fechaEstimadaInicio; // REQUERIDO
    LocalDateTime fechaEstimadaFin;    // REQUERIDO
}

// Salida
RutaTentativaDTO {
    List<TramoTentativoDTO> tramos;
    double costoEstimadoTotal;
    double tiempoEstimadoTotal;        // en horas (calculado por Google Maps)
    double distanciaTotal;             // en km (calculado por Google Maps)
    int cantidadTramos;
    int cantidadDepositos;
    String tipoRuta;                   // DIRECTA, CON_PARADAS
    String descripcion;                // Descripción generada automáticamente
}

TramoTentativoDTO {
    int orden;
    String tipo;                       // ORIGEN-DESTINO, ORIGEN-DEPOSITO, etc.
    Coordenada puntoInicio;            // Latitud y longitud de inicio
    Coordenada puntoFin;               // Latitud y longitud de fin
    double distanciaKm;                // Distancia real calculada con Google Maps
    double tiempoEstimadoHoras;        // Tiempo real calculado con Google Maps
    double costoAproximado;            // Costo estimado ($5/km)
    String observaciones;              // "Ruta directa... (calculada con Google Maps)"
}

RutaDTO {
    Long id;
    Long solicitudId;
    int cantidadTramos;
    int cantidadDepositos;
    double distanciaTotal;
    double tiempoEstimadoTotal;
    double costoEstimado;
    List<TramoDTO> tramos;
}
```

---

### 🛤️ Recurso: Tramos

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/tramos` | Transportista, Operador | Listar tramos con filtros | Query: `rutaId`, `camionId`, `estado` | `List<TramoDTO>` | 200 | ✅ Implementado |
| **GET** | `/tramos/{id}` | Transportista, Operador | Obtener tramo por ID | `id: Long` | `TramoDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/tramos/{id}/asignar-camion` | Operador | **[RF#6]** Asignar camión a tramo | `id: Long`, `AsignacionCamionDTO` (body) | `TramoDTO` | 200, 400, 404 | ✅ Implementado |
| **POST** | `/tramos/{id}/iniciar` | Transportista | **[RF#8]** Registrar inicio de tramo | `id: Long` | `TramoDTO` | 200, 400, 404 | ✅ Implementado |
| **POST** | `/tramos/{id}/finalizar` | Transportista | **[RF#8]** Registrar fin de tramo + Cálculo de costo real | `id: Long` | `TramoDTO` | 200, 400, 404 | ✅ **Implementado con FlotaServiceClient** |
| **GET** | `/tramos/transportistas/{camionId}/tramos` | Transportista | **[RF#7]** Ver tramos asignados a transportista | `camionId: Long` | `List<TramoDTO>` | 200, 404 | ✅ **Implementado** |

#### DTOs - Tramos *(✅ Implementado)*

```java
// Entrada
AsignacionCamionDTO {
    Long camionId;                    // REQUERIDO - ID del camión en servicio-flota
}

InicioTramoDTO {
    LocalDateTime fechaHoraInicio;    // OPCIONAL - Si no se envía, usa fecha actual
}

FinTramoDTO {
    LocalDateTime fechaHoraFin;       // OPCIONAL - Si no se envía, usa fecha actual
    String observaciones;             // OPCIONAL
}

// Salida
TramoDTO {
    Long id;
    Long rutaId;
    int orden;
    String tipo;
    String estado;
    String puntoInicio;
    double latitudInicio;
    double longitudInicio;
    String puntoFin;
    double latitudFin;
    double longitudFin;
    double distanciaKm;
    double tiempoEstimadoHoras;
    double costoAproximado;
    double costoReal;
    LocalDateTime fechaEstimadaInicio;
    LocalDateTime fechaEstimadaFin;
    LocalDateTime fechaHoraInicio;
    LocalDateTime fechaHoraFin;
    CamionReferenceDTO camion;
    DepositoReferenceDTO depositoOrigen;
    DepositoReferenceDTO depositoDestino;
}
```

---

### 🌍 Integración Google Maps API

**Estado**: ✅ **Completamente Implementado y Funcional**

La integración con Google Maps Distance Matrix API está funcionando correctamente:

- **RestClient Configuration**: Bean configurado en `RestClientConfig.java`
- **DTOs de Google Maps**: 5 DTOs implementados (`Distance`, `Duration`, `Element`, `Row`, `GoogleDistanceMatrixResponse`)
- **Cliente HTTP**: `GoogleMapsClient` con manejo de errores y Optional
- **Servicio de Negocio**: `GoogleMapsService` procesa respuestas de la API
- **Uso en RutaService**: Cálculo de distancias y tiempos reales
- **API Key**: Configurada en `application.properties` (protegida con .gitignore)

**Endpoints que usan Google Maps**:
- `GET /solicitudes/{id}/rutas/tentativas` - Calcula distancias y tiempos reales

**Ejemplo de respuesta real**:
```json
{
  "tramos": [{
    "orden": 1,
    "tipo": "ORIGEN-DESTINO",
    "puntoInicio": {"latitud": -31.4201, "longitud": -64.1888},
    "puntoFin": {"latitud": -34.6037, "longitud": -58.3816},
    "distanciaKm": 695.477,
    "tiempoEstimadoHoras": 7.35,
    "costoAproximado": 3477.38,
    "observaciones": "Ruta directa... (calculada con Google Maps)"
  }],
  "costoEstimadoTotal": 3477.38,
  "tiempoEstimadoTotal": 7.35,
  "distanciaTotal": 695.477
}
```

---

### 🔄 Recurso: Referencias (Sincronización entre servicios)

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **POST** | `/camiones-reference/sync` | Sistema | Sincronizar camión desde servicio-flota | `CamionSyncDTO` (body) | `CamionReferenceDTO` | 201, 400 | 🟡 Pendiente (Lógica) |
| **GET** | `/camiones-reference` | Sistema | Listar referencias de camiones | - | `List<CamionReferenceDTO>` | 200 | ✅ Implementado |
| **POST** | `/depositos-reference/sync` | Sistema | Sincronizar depósito desde servicio-flota | `DepositoSyncDTO` (body) | `DepositoReferenceDTO` | 201, 400 | 🟡 Pendiente (Lógica) |
| **GET** | `/depositos-reference` | Sistema | Listar referencias de depósitos | - | `List<DepositoReferenceDTO>` | 200 | ✅ Implementado |

#### DTOs - Cálculos *(🟡 Pendiente - Lógica)*

```java
// Entrada
CalculoCostoRequestDTO {
    Long contenedorId;                // REQUERIDO
    List<Long> tramoIds;              // REQUERIDO
}

DistanciaRequestDTO {
    CoordenadaDTO origen;             // REQUERIDO
    CoordenadaDTO destino;            // REQUERIDO
}

CoordenadaDTO {
    double latitud;
    double longitud;
}

// Salida
CalculoCostoResponseDTO {
    double costoTotal;
    DesgloseDTO desglose;
}

DesgloseDTO {
    double costoCombustible;
    double costoKilometraje;
    double costoEstadia;
    double cargoGestion;
    List<CostoTramoDTO> costosPorTramo;
}

CostoTramoDTO {
    int orden;
    double distanciaKm;
    double costoCombustible;
    double costoKilometraje;
    double costoEstadia;
}

DistanciaResponseDTO {
    double distanciaKm;
    double tiempoEstimadoHoras;
    String proveedor;                 // "Google Maps API"
}
```

---

## 🌐 API Gateway

**Puerto**: 8080 (Pendiente implementación - Entrega Final)  
**Tecnología**: Spring Cloud Gateway  
**Responsabilidades**:
- Punto de entrada único
- Enrutamiento a microservicios
- Validación de tokens JWT (Keycloak)
- Rate limiting
- CORS

### Rutas de Enrutamiento

| Ruta Externa | Microservicio Destino | Puerto | Función |
|--------------|----------------------|--------|---------|
| `/api/camiones/**` | servicio-flota | 8081 | Gestión de camiones |
| `/api/depositos/**` | servicio-flota | 8081 | Gestión de depósitos |
| `/api/tarifas/**` | servicio-flota | 8081 | Gestión de tarifas |
| `/api/clientes/**` | servicio-operaciones | 8082 | Gestión de clientes |
| `/api/contenedores/**` | servicio-operaciones | 8082 | Gestión de contenedores |
| `/api/solicitudes/**` | servicio-operaciones | 8082 | Gestión de solicitudes |
| `/api/rutas/**` | servicio-operaciones | 8082 | Gestión de rutas |
| `/api/tramos/**` | servicio-operaciones | 8082 | Gestión de tramos |
| `/api/transportistas/**` | servicio-operaciones | 8082 | Consultas de transportistas |
| `/api/calculos/**` | servicio-operaciones | 8082 | Servicios de cálculo |

---

## 📊 Códigos HTTP Utilizados

| Código | Descripción | Uso |
|--------|-------------|-----|
| **200** | OK | Operación exitosa (GET, PUT, PATCH) |
| **201** | Created | Recurso creado exitosamente (POST) |
| **204** | No Content | Recurso eliminado exitosamente (DELETE) |
| **400** | Bad Request | Datos de entrada inválidos o validación fallida |
| **401** | Unauthorized | Token JWT inválido o ausente |
| **403** | Forbidden | Usuario sin permisos para el recurso |
| **404** | Not Found | Recurso no encontrado |
| **409** | Conflict | Conflicto (ej: email duplicado, CUIT existente) |
| **500** | Internal Server Error | Error interno del servidor |
| **503** | Service Unavailable | Servicio externo no disponible (ej: Google Maps) |

---

## 🎯 Mapeo de Requerimientos Funcionales

Según **Enunciado - Requerimientos Funcionales Mínimos**:

| RF# | Requerimiento | Endpoint(s) | Microservicio | Estado |
|-----|--------------|------------|---------------|--------|
| **RF#1** | Registrar nueva solicitud de transporte | `POST /solicitudes` | Operaciones | ✅ **Implementado** |
| **RF#2** | Consultar estado del transporte | `GET /solicitudes/{id}/estado`<br>`GET /contenedores/{id}/estado` | Operaciones | ✅ **Implementado** |
| **RF#3** | Consultar rutas tentativas | `GET /solicitudes/{id}/rutas/tentativas` | Operaciones | ✅ **Implementado con Google Maps** |
| **RF#4** | Asignar ruta con tramos | `POST /solicitudes/{id}/asignar-ruta` | Operaciones | ✅ **Implementado** |
| **RF#5** | Consultar contenedores pendientes | `GET /contenedores/pendientes` | Operaciones | ✅ **Implementado** |
| **RF#6** | Asignar camión a tramo | `POST /tramos/{id}/asignar-camion` | Operaciones | ✅ **Implementado** |
| **RF#7** | Determinar inicio/fin de tramo | `POST /tramos/{id}/iniciar`<br>`POST /tramos/{id}/finalizar` | Operaciones | ✅ **Implementado** (RF#8) |
| **RF#8** | Calcular costo total del tramo | Lógica interna en `POST /tramos/{id}/finalizar` | Operaciones | ✅ **Implementado con FlotaServiceClient** |
| **RF#9** | Registrar costo/tiempo final | `PATCH /solicitudes/{id}/finalizar` | Operaciones | ✅ **Implementado** |
| **RF#10** | Registrar/actualizar depósitos, camiones, tarifas | `POST/PUT/DELETE /camiones`<br>`POST/PUT/DELETE /depositos`<br>`POST/PUT /tarifas` | Flota | ✅ **Implementado** |
| **RF#11** | Validar capacidad de camión | Lógica interna en asignación | Operaciones | ✅ **Implementado** (RF#6) |

**Notas de Implementación**: 

- **RF#7** se implementó con dos funcionalidades:
  1. Endpoints: `POST /tramos/{id}/iniciar` y `POST /tramos/{id}/finalizar`
  2. Consulta de tramos: `GET /tramos/transportistas/{camionId}/tramos`

- **RF#8** (Cálculo de costos) se implementó como lógica interna en `finalizarTramo()`:
  - Obtiene la **tarifa activa** desde servicio-flota (`GET /api/tarifas/activa`)
  - Obtiene los **datos del camión** desde servicio-flota (`GET /api/camiones/{id}`)
  - Calcula el **costo real** con la fórmula:
    ```
    costoReal = cargoGestionPorTramo + (costoPorKm × distanciaKm) + (consumoCombustiblePorKm × distanciaKm × precioLitroCombustible)
    ```
  - Almacena el resultado en el campo `costoReal` del tramo
  - Utiliza `FlotaServiceClient` para comunicación entre microservicios
  - **Logging comprehensivo** en cada paso del cálculo

- **RF#9** (Finalización de solicitud) se implementó en `SolicitudService.finalizarSolicitud()`:
  - Valida que todos los tramos estén FINALIZADOS
  - Calcula **costo total** sumando `costoReal` de todos los tramos
  - Calcula **tiempo real total** con `Duration.between(fechaRealInicio, fechaRealFin)`
  - Actualiza: `solicitud.costoFinal`, `solicitud.tiempoReal`, `solicitud.estado = "ENTREGADA"`
  - **Logging detallado** con DEBUG para valores calculados
  - Endpoint: `PATCH /api/solicitudes/{id}/finalizar`

---

## 🔐 Seguridad y Roles (Entrega Final)

### Roles definidos:

1. **Cliente**
   - Puede crear solicitudes
   - Puede consultar estado de sus contenedores/solicitudes
   - Puede registrar/actualizar sus datos

2. **Transportista**
   - Puede ver sus tramos asignados
   - Puede registrar inicio/fin de tramos
   - Solo acceso a sus propios datos

3. **Operador/Administrador**
   - Acceso completo a gestión de recursos
   - Puede asignar rutas y camiones
   - Puede consultar todos los datos del sistema

4. **Sistema** (Internal)
   - Comunicación entre microservicios
   - Sincronización de referencias
   - Cálculos automáticos

---

## � Mejoras Implementadas en Esta Sesión

### 📊 Logging SLF4J Comprehensivo

**Estado**: ✅ **Completamente Implementado**

Se agregó logging profesional usando SLF4J en **todos los servicios** de ambos microservicios:

#### Servicio-Flota (3 servicios):
1. **CamionService**: 7 métodos con logging
2. **DepositoService**: 5 métodos con logging  
3. **TarifaService**: 13 métodos con logging

#### Servicio-Operaciones (5 servicios):
1. **ClienteService**: 5 métodos con logging
2. **ContenedorService**: 7 métodos con logging (incluyendo RF#2, RF#5)
3. **RutaService**: 5 métodos con logging (incluyendo RF#3 con Google Maps)
4. **SolicitudService**: Logging comprehensivo en `finalizarSolicitud()` (RF#9)
5. **TramoService**: Ya tenía logger implementado

**Patrón de Logging Utilizado**:
```java
private static final Logger logger = LoggerFactory.getLogger(NombreClase.class);

// INFO: Operaciones exitosas con contadores
logger.info("Obteniendo todos los camiones");
logger.info("Se encontraron {} camiones", camiones.size());

// WARN: Recursos no encontrados
logger.warn("Camión no encontrado con ID: {}", id);

// ERROR: Errores y excepciones con contexto
logger.error("Error al asignar ruta a solicitud {}: {}", solicitudId, e.getMessage());

// DEBUG: Valores calculados y detalles
logger.debug("Costo total calculado: {}, Tiempo total: {}", costoTotal, tiempoTotal);
```

### 💰 RF#9: Finalización de Solicitudes - IMPLEMENTADO

**Endpoint**: `PATCH /api/solicitudes/{id}/finalizar`

**Funcionalidad Completa**:
1. ✅ Valida que la solicitud existe y está EN_TRANSITO
2. ✅ Valida que tiene ruta asignada con tramos
3. ✅ Valida que TODOS los tramos estén FINALIZADOS
4. ✅ Calcula el **costo total** sumando `costoReal` de todos los tramos
5. ✅ Calcula el **tiempo real total** usando `Duration.between(fechaRealInicio, fechaRealFin)`
6. ✅ Actualiza: `costoFinal`, `tiempoReal`, `estado = "ENTREGADA"`
7. ✅ Logging comprehensivo en cada paso del proceso

**Método Implementado en SolicitudService**:
```java
public SolicitudDTO finalizarSolicitud(Long solicitudId, FinalizacionSolicitudDTO finalizacionDTO) {
    logger.info("Iniciando finalización de solicitud con ID: {}", solicitudId);
    
    // Validación de solicitud
    Solicitud solicitud = solicitudRepository.findById(solicitudId)
        .orElseThrow(() -> {
            logger.warn("Solicitud no encontrada con ID: {}", solicitudId);
            return new RuntimeException("Solicitud no encontrada");
        });
    
    // Validación de estado
    if (!"EN_TRANSITO".equals(solicitud.getEstado())) {
        logger.error("La solicitud {} no está EN_TRANSITO", solicitudId);
        throw new IllegalStateException("Solo se pueden finalizar solicitudes EN_TRANSITO");
    }
    
    // Validación de ruta y tramos
    Ruta ruta = solicitud.getRuta();
    if (ruta == null || ruta.getTramos().isEmpty()) {
        logger.error("La solicitud {} no tiene ruta o tramos asignados", solicitudId);
        throw new IllegalStateException("La solicitud no tiene ruta asignada");
    }
    
    // Validación de tramos finalizados
    boolean todosFinalizados = ruta.getTramos().stream()
        .allMatch(t -> "FINALIZADO".equals(t.getEstado()));
    
    if (!todosFinalizados) {
        logger.error("No todos los tramos están finalizados para solicitud {}", solicitudId);
        throw new IllegalStateException("Todos los tramos deben estar finalizados");
    }
    
    // Cálculo de costo total
    double costoTotal = ruta.getTramos().stream()
        .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
        .sum();
    
    // Cálculo de tiempo real total
    LocalDateTime fechaRealInicio = ruta.getTramos().stream()
        .map(Tramo::getFechaRealInicio)
        .filter(Objects::nonNull)
        .min(LocalDateTime::compareTo)
        .orElse(null);
    
    LocalDateTime fechaRealFin = ruta.getTramos().stream()
        .map(Tramo::getFechaRealFin)
        .filter(Objects::nonNull)
        .max(LocalDateTime::compareTo)
        .orElse(null);
    
    double tiempoTotalHoras = 0.0;
    if (fechaRealInicio != null && fechaRealFin != null) {
        Duration duracion = Duration.between(fechaRealInicio, fechaRealFin);
        tiempoTotalHoras = duracion.toMinutes() / 60.0;
    }
    
    logger.debug("Cálculos para solicitud {}: {} tramos, costoTotal={}, tiempoTotal={} horas",
        solicitudId, ruta.getTramos().size(), costoTotal, tiempoTotalHoras);
    
    // Actualizar solicitud
    solicitud.setCostoFinal(costoTotal);
    solicitud.setTiempoReal(tiempoTotalHoras);
    solicitud.setEstado("ENTREGADA");
    
    if (finalizacionDTO != null && finalizacionDTO.getObservaciones() != null) {
        solicitud.setObservaciones(
            solicitud.getObservaciones() + "\n" + finalizacionDTO.getObservaciones()
        );
    }
    
    Solicitud solicitudFinalizada = solicitudRepository.save(solicitud);
    
    logger.info("Solicitud {} finalizada exitosamente. Estado: ENTREGADA, Costo: ${}, Tiempo: {} horas",
        solicitudId, costoTotal, tiempoTotalHoras);
    
    return solicitudMapper.toDTO(solicitudFinalizada);
}
```

### 🧪 Scripts de Prueba End-to-End

**Estado**: ✅ **Implementados y Documentados**

Se crearon 3 herramientas de testing:

1. **test-e2e-flow.http** (REST Client para VS Code)
   - 22 pasos detallados
   - Formato `.http` para extensión REST Client
   - Variables para capturar IDs
   - Prueba flujo completo: RF#1, RF#2, RF#3, RF#6, RF#7, RF#8, RF#9

2. **test-e2e-flow.sh** (Bash automatizado)
   - Script completamente automatizado
   - Captura automática de IDs con `jq`
   - Output con colores
   - Resumen final con estadísticas

3. **test-e2e-simple.ps1** (PowerShell)
   - Script Windows automatizado
   - 7 pasos principales
   - Manejo de errores
   - Resumen final detallado

4. **demo-rutas.txt** (Comandos rápidos)
   - Comando único para demostrar RF#3
   - Ejemplos de todas las funcionalidades
   - Listo para copy-paste

### 🐛 Correcciones de Bugs

**Bug Crítico Corregido**: Error en `TramoRepository`

**Problema Original**:
```java
// ❌ INCORRECTO - CamionReference no tiene campo 'camionId'
List<Tramo> findByCamionReference_CamionIdAndEstadoNotIn(Long camionId, List<String> estados);
```

**Solución Implementada**:
```java
// ✅ CORRECTO - CamionReference tiene campo 'id'
List<Tramo> findByCamionReference_IdAndEstadoNotIn(Long camionId, List<String> estados);
```

**Archivos Corregidos**:
- `servicio-operaciones/repositories/TramoRepository.java`
- `servicio-operaciones/services/TramoService.java`

**Resultado**: ✅ Ambos microservicios compilan exitosamente (BUILD SUCCESS)

### 📚 Documentación Actualizada

**Archivos Creados/Actualizados**:
1. ✅ `Endpoints-Documentacion.md` - Este archivo (actualizado)
2. ✅ `test-e2e-flow.http` - Pruebas REST Client
3. ✅ `test-e2e-flow.sh` - Script Bash automatizado
4. ✅ `test-e2e-simple.ps1` - Script PowerShell
5. ✅ `demo-rutas.txt` - Comandos de demostración

### 🎯 Estado Final de Requerimientos Funcionales

| RF# | Descripción | Estado | Evidencia |
|-----|-------------|--------|-----------|
| RF#1 | Registrar solicitud | ✅ | POST /api/solicitudes |
| RF#2 | Consultar estado | ✅ | GET /api/contenedores/{id}/estado<br>GET /api/solicitudes/{id}/estado |
| RF#3 | Rutas tentativas | ✅ | GET /api/solicitudes/{id}/rutas/tentativas<br>Google Maps API integrada |
| RF#4 | Asignar ruta | ✅ | POST /api/solicitudes/{id}/asignar-ruta |
| RF#5 | Contenedores pendientes | ✅ | GET /api/contenedores/pendientes |
| RF#6 | Asignar camión | ✅ | POST /api/tramos/{id}/asignar-camion |
| RF#7 | Tramos transportista | ✅ | GET /api/tramos/transportistas/{id}/tramos |
| RF#8 | Iniciar/Finalizar tramo | ✅ | POST /api/tramos/{id}/iniciar<br>POST /api/tramos/{id}/finalizar |
| RF#9 | Finalizar solicitud | ✅ | PATCH /api/solicitudes/{id}/finalizar |
| RF#10 | Gestión recursos | ✅ | CRUD Camiones, Depósitos, Tarifas |

**Cobertura de Logging**: 100% de los servicios tienen logging SLF4J

## �📝 Notas Adicionales

### Validaciones Importantes:

1. **Al crear solicitud**:
   - Validar que cliente existe o crear uno nuevo
   - Validar coordenadas (formato lat/lng válido)
   - Validar peso y volumen > 0

2. **Al asignar camión a tramo**:
   - Validar que camión esté disponible
   - Validar que capacidad del camión sea suficiente (peso y volumen)
   - Cambiar estado del tramo a "ASIGNADO"
   - Marcar camión como no disponible

3. **Al iniciar tramo**:
   - Validar que tramo tenga camión asignado
   - Validar que estado sea "ASIGNADO"
   - Cambiar estado a "INICIADO"
   - Actualizar estado del contenedor

4. **Al finalizar tramo**:
   - Validar que estado sea "INICIADO"
   - Calcular costo real del tramo usando FlotaServiceClient
   - Fórmula: `costoReal = cargoGestion + (costoPorKm × distanciaKm) + (consumoCombustible × distanciaKm × precioCombustible)`
   - Cambiar estado a "FINALIZADO"
   - Marcar camión como disponible
   - Actualizar estado del contenedor a "ENTREGADO"

5. **Al finalizar solicitud (RF#9)**:
   - Validar que todos los tramos estén FINALIZADOS
   - Calcular costo total sumando `costoReal` de todos los tramos
   - Calcular tiempo real total con `Duration.between()`
   - Actualizar: `costoFinal`, `tiempoReal`, `estado = "ENTREGADA"`
   - Logging comprehensivo de todo el proceso

### Comunicación entre Microservicios:

- **Servicio-Operaciones → Servicio-Flota**:
  - Consultar camiones disponibles
  - Consultar depósitos
  - Consultar tarifa activa
  - Actualizar disponibilidad de camiones

- **Sincronización**:
  - Al crear/actualizar camión en Flota → Sincronizar en Operaciones
  - Al crear/actualizar depósito en Flota → Sincronizar en Operaciones

### ✅ Integración con Google Maps (IMPLEMENTADA):

**Estado**: ✅ Completamente Funcional

- **API**: Google Maps Distance Matrix API
- **Base URL**: `https://maps.googleapis.com/maps/api`
- **Configuración**: RestClient con inyección de dependencias
- **Componentes**:
  - `GoogleMapsClient`: Cliente HTTP con manejo de Optional
  - `GoogleMapsService`: Servicio de negocio para procesamiento
  - 5 DTOs: `Distance`, `Duration`, `Element`, `Row`, `GoogleDistanceMatrixResponse`
  - `RestClientConfig`: Bean de configuración con URL base

**Funcionalidad**:
- Cálculo de distancias reales en kilómetros
- Cálculo de tiempos estimados en horas
- Conversión automática de unidades (metros→km, segundos→horas)
- Manejo robusto de errores con Optional

**Uso**:
- Integrado en `RutaService.calcularRutasTentativas()`
- Endpoint: `GET /solicitudes/{id}/rutas/tentativas`
- Ejemplo: Córdoba → Buenos Aires = 695.48 km, 7.35 horas

**Seguridad**:
- API Key protegida con `.gitignore`
- Configurada en `application.properties` (no versionada)

---

**Fin del Documento**

