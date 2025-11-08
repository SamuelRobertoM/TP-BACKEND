# FlotaServiceClient - Comunicación entre Microservicios

## 📋 Descripción

Este cliente permite la comunicación desde `servicio-operaciones` hacia `servicio-flota` usando RestClient de Spring Boot para obtener información de **Tarifas** y **Camiones**.

## 🏗️ Arquitectura

```
servicio-operaciones (Puerto 8082)
         ↓
   FlotaServiceClient (RestClient)
         ↓
servicio-flota (Puerto 8081)
```

## ⚙️ Configuración

### 1. application.properties

```properties
# Servicio Flota Configuration
servicio-flota.base-url=http://localhost:8081/api
```

### 2. RestClientConfig.java

Bean configurado para inyectar el RestClient con la URL base de servicio-flota:

```java
@Bean
public RestClient flotaRestClient(RestClient.Builder builder) {
    return builder
            .baseUrl(servicioFlotaBaseUrl)
            .build();
}
```

## 📦 DTOs Creados

### `dtos/flota/TarifaDTO.java`
```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TarifaDTO {
    private Long id;
    private double costoKmBase;
    private double precioLitroCombustible;
    private double cargoGestionPorTramo;
    private LocalDateTime vigenciaDesde;
    private LocalDateTime vigenciaHasta;
    private boolean activa;
}
```

### `dtos/flota/CamionDTO.java`
```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CamionDTO {
    private Long id;
    private String dominio;
    private String nombreTransportista;
    private String telefono;
    private double capacidadPeso;
    private double capacidadVolumen;
    private double consumoCombustiblePorKm;
    private boolean disponible;
    private double costoPorKm;
}
```

## 🔌 FlotaServiceClient - API

### Constructor
```java
public FlotaServiceClient(@Qualifier("flotaRestClient") RestClient restClient)
```

### Métodos

#### 1. Obtener Tarifa Activa
```java
public Optional<TarifaDTO> obtenerTarifaActiva()
```

**Endpoint consumido:** `GET /api/tarifas/actual`

**Uso:**
```java
@Autowired
private FlotaServiceClient flotaServiceClient;

public void ejemplo() {
    Optional<TarifaDTO> tarifa = flotaServiceClient.obtenerTarifaActiva();
    
    tarifa.ifPresent(t -> {
        System.out.println("Costo por km: " + t.getCostoKmBase());
        System.out.println("Precio combustible: " + t.getPrecioLitroCombustible());
    });
}
```

#### 2. Obtener Camión por ID
```java
public Optional<CamionDTO> obtenerCamionPorId(Long camionId)
```

**Endpoint consumido:** `GET /api/camiones/{camionId}`

**Uso:**
```java
@Autowired
private FlotaServiceClient flotaServiceClient;

public void ejemplo() {
    Long camionId = 1L;
    Optional<CamionDTO> camion = flotaServiceClient.obtenerCamionPorId(camionId);
    
    camion.ifPresent(c -> {
        System.out.println("Dominio: " + c.getDominio());
        System.out.println("Disponible: " + c.isDisponible());
        System.out.println("Costo por km: " + c.getCostoPorKm());
    });
}
```

## 🧪 Endpoints de Prueba

Se creó `FlotaTestController` con endpoints de prueba:

### 1. Obtener Tarifa Actual
```bash
GET http://localhost:8082/api/test-flota/tarifa-actual
```

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "costoKmBase": 5.0,
  "precioLitroCombustible": 150.0,
  "cargoGestionPorTramo": 500.0,
  "vigenciaDesde": "2025-10-01T00:00:00",
  "vigenciaHasta": null,
  "activa": true
}
```

### 2. Obtener Camión por ID
```bash
GET http://localhost:8082/api/test-flota/camion/1
```

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "dominio": "ABC123",
  "nombreTransportista": "Juan Perez",
  "telefono": "351-1234567",
  "capacidadPeso": 5000.0,
  "capacidadVolumen": 50.0,
  "consumoCombustiblePorKm": 0.3,
  "disponible": true,
  "costoPorKm": 1.5
}
```

**Respuesta no encontrado (404):**
```
(Sin contenido)
```

## 🔍 Manejo de Errores

El cliente maneja los siguientes escenarios:

### 1. Servicio Flota no disponible
```
[ERROR] Error inesperado al obtener tarifa activa desde servicio-flota
```

### 2. Recurso no encontrado (404)
```
[WARN] Camión con ID 999 no encontrado en servicio-flota (404).
```

### 3. Error HTTP genérico
```
[ERROR] Error HTTP al obtener camión 1: 500 - Internal Server Error
```

## 📝 Logging

El cliente incluye logs informativos:

- `DEBUG`: Antes de cada llamada HTTP
- `INFO`: Cuando se obtiene un recurso exitosamente
- `WARN`: Cuando un recurso no existe (404)
- `ERROR`: Para errores HTTP o excepciones inesperadas

## 🚀 Ejemplo de Integración Real

### Caso de Uso: Calcular Costo de Ruta

```java
@Service
public class RutaService {
    
    @Autowired
    private FlotaServiceClient flotaServiceClient;
    
    public double calcularCostoRuta(Long rutaId, Long camionId) {
        // 1. Obtener tarifa activa
        TarifaDTO tarifa = flotaServiceClient.obtenerTarifaActiva()
                .orElseThrow(() -> new RuntimeException("No hay tarifa activa"));
        
        // 2. Obtener información del camión
        CamionDTO camion = flotaServiceClient.obtenerCamionPorId(camionId)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));
        
        // 3. Obtener distancia de la ruta (desde BD local)
        Ruta ruta = rutaRepository.findById(rutaId).orElseThrow();
        double distanciaKm = ruta.getDistanciaKm();
        
        // 4. Calcular costos
        double costoCombustible = distanciaKm * camion.getConsumoCombustiblePorKm() 
                                  * tarifa.getPrecioLitroCombustible();
        double costoBase = distanciaKm * tarifa.getCostoKmBase();
        double costoTotal = costoCombustible + costoBase;
        
        return costoTotal;
    }
}
```

## ✅ Checklist de Implementación

- [x] Configurar URL base en `application.properties`
- [x] Crear bean `flotaRestClient` en `RestClientConfig`
- [x] Crear DTOs en paquete `dtos.flota`
- [x] Implementar `FlotaServiceClient` con manejo de errores
- [x] Crear controlador de prueba `FlotaTestController`
- [x] Compilación exitosa
- [ ] Probar con servicio-flota levantado
- [ ] Integrar en servicios de negocio (RutaService, SolicitudService, etc.)

## 🔧 Requisitos

1. **servicio-flota** debe estar corriendo en `http://localhost:8081`
2. **servicio-flota** debe tener:
   - Endpoint `GET /api/tarifas/actual`
   - Endpoint `GET /api/camiones/{id}`
3. Debe existir al menos una tarifa activa en servicio-flota
4. Deben existir camiones registrados en servicio-flota

## 📚 Referencias

- Spring RestClient Documentation
- Microservices Communication Patterns
- Service-to-Service Communication Best Practices
