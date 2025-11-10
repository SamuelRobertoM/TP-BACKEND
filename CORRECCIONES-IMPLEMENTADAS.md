# Correcciones Implementadas - Sistema TPI Backend

Este documento resume las correcciones realizadas para que el flujo E2E funcione correctamente.

## 1. Servicio Flota

### SecurityConfig.java
**Problema:** El endpoint `/api/tarifas/actual` requería autenticación, bloqueando las llamadas desde servicio-operaciones.

**Solución:** Permitir acceso público al endpoint de tarifa activa para comunicación entre microservicios.

```java
// Endpoint público para consulta de tarifa activa (usado por otros microservicios)
.requestMatchers(HttpMethod.GET, "/api/tarifas/actual").permitAll()
```

**Ubicación:** `servicio-flota/src/main/java/utn/frc/isi/backend/tpi_Integrador/config/SecurityConfig.java`

---

## 2. Servicio Operaciones

### 2.1 RestClientConfig.java
**Problema:** No se propagaba el JWT token en las llamadas entre microservicios.

**Solución:** Agregar interceptor para propagar automáticamente el token JWT.

```java
@Bean
public RestClient flotaRestClient(RestClient.Builder builder) {
    return builder
            .baseUrl(servicioFlotaBaseUrl)
            .requestInterceptor((request, body, execution) -> {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                    String token = jwtAuth.getToken().getTokenValue();
                    request.getHeaders().setBearerAuth(token);
                }
                return execution.execute(request, body);
            })
            .build();
}
```

**Ubicación:** `servicio-operaciones/src/main/java/utn/frc/isi/backend/tpi_Integrador/config/RestClientConfig.java`

### 2.2 application.properties
**Problema:** La base URL no incluía el prefijo `/api`, causando errores 404.

**Solución:** Agregar `/api` a la base URL.

```properties
servicio-flota.base-url=http://localhost:8081/api
```

**Ubicación:** `servicio-operaciones/src/main/resources/application.properties`

### 2.3 CamionReference.java
**Problema:** Faltaban campos para sincronizar correctamente con servicio-flota.

**Solución:** Agregar campos `camionIdFlota` y `costoPorKm`.

```java
@Column(name = "camion_id_flota")
private Long camionIdFlota;

@Column(name = "costo_por_km")
private Double costoPorKm;
```

**Ubicación:** `servicio-operaciones/src/main/java/utn/frc/isi/backend/tpi_Integrador/models/CamionReference.java`

### 2.4 TramoService.java
**Problema:** Al finalizar un tramo, se finalizaba automáticamente la solicitud, impidiendo el flujo correcto.

**Solución:** Eliminar la lógica que finalizaba automáticamente la solicitud. Ahora solo finaliza el tramo y calcula su costo.

```java
// ANTES: Finalizaba automáticamente la solicitud si todos los tramos estaban finalizados
// AHORA: Solo finaliza el tramo. La solicitud se finaliza explícitamente con PATCH /solicitudes/{id}/finalizar
```

**Ubicación:** `servicio-operaciones/src/main/java/utn/frc/isi/backend/tpi_Integrador/services/TramoService.java`

---

## 3. Colección Postman

### Paso 9 agregado: Crear Referencia de Camión
**Problema:** Faltaba crear la referencia del camión en servicio-operaciones antes de asignarlo al tramo.

**Solución:** Agregar paso 9 que crea la referencia sincronizando datos desde servicio-flota.

**Request:**
```
POST {{gateway_url}}/api/operaciones/camion-references
Body: {
  "camionIdFlota": {{camion_id}},
  "dominio": "{{camion_dominio}}",
  "capacidadPeso": {{camion_capacidad_peso}},
  "capacidadVolumen": {{camion_capacidad_volumen}},
  "disponible": true,
  "costoPorKm": {{camion_costo_km}}
}
```

---

## Flujo E2E Correcto

1. Login → Obtener token JWT
2. Crear Tarifa → Configurar costos
3. Crear Camión → Registrar vehículo
4. Crear Depósito → Punto intermedio
5. Crear Cliente → Datos del solicitante
6. Crear Contenedor → Carga a transportar
7. Crear Solicitud → Pedido de transporte
8. Crear Ruta con Google Maps → Calcular distancia y tiempo
9. **Crear Referencia de Camión** → Sincronizar con servicio-operaciones
10. Asignar Camión a Tramo → Vincular vehículo
11. Iniciar Tramo → Comenzar viaje (Solicitud → EN_TRANSITO)
12. Finalizar Tramo → Calcular costo del tramo
13. Finalizar Solicitud → Calcular costo total (Solicitud → ENTREGADA)

---

## Verificación

Para verificar que todo funciona:

1. Reiniciar ambos servicios (servicio-flota y servicio-operaciones)
2. En Postman, ejecutar la carpeta "🚀 E2E Complete Flow" con Collection Runner
3. Todos los 13 pasos deben pasar exitosamente

---

## Notas Técnicas

- **Comunicación entre microservicios:** Se usa RestClient con propagación automática de JWT
- **Seguridad:** El endpoint `/api/tarifas/actual` es público para permitir comunicación interna
- **Sincronización:** CamionReference mantiene una copia de datos de servicio-flota para evitar dependencias en tiempo de ejecución
- **Separación de responsabilidades:** Finalizar tramo ≠ Finalizar solicitud (dos operaciones distintas)
