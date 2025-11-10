# Guía de Uso - Colección Postman TPI Backend

## Importar la Colección

1. Abrir Postman
2. Click en "Import" → Seleccionar `TPI-Backend-Postman-Collection.json`
3. La colección se importará con todas las carpetas y variables configuradas

## Variables de Entorno

La colección usa variables que se configuran automáticamente:

- `{{gateway_url}}` - URL del API Gateway (default: http://localhost:8080)
- `{{token}}` - JWT token (se obtiene automáticamente en el paso 1)
- `{{solicitud_id}}`, `{{ruta_id}}`, `{{tramo_id}}` - IDs generados durante el flujo

## Estructura de la Colección

### 📁 Authentication
Login con Keycloak para obtener el JWT token.

### 📁 Servicio Flota
- **Tarifas:** Gestión de costos de transporte
- **Camiones:** Registro y consulta de vehículos
- **Depósitos:** Puntos intermedios de almacenamiento

### 📁 Servicio Operaciones
- **Clientes:** Gestión de clientes
- **Contenedores:** Cargas a transportar
- **Solicitudes:** Pedidos de transporte
- **Rutas:** Planificación con Google Maps
- **Tramos:** Segmentos del viaje
- **Camion References:** Sincronización entre servicios

### 📁 🚀 E2E Complete Flow
**Flujo completo automatizado de 13 pasos** que demuestra todo el sistema funcionando.

## Ejecutar el Flujo E2E

### Opción 1: Collection Runner (Recomendado)
1. Click derecho en "🚀 E2E Complete Flow"
2. Seleccionar "Run folder"
3. Click en "Run TPI Backend..."
4. Ver los resultados de los 13 pasos

### Opción 2: Manual
Ejecutar los pasos del 1 al 13 en orden:
1. Login
2. Crear Tarifa
3. Crear Camión
4. Crear Depósito
5. Crear Cliente
6. Crear Contenedor
7. Crear Solicitud
8. Crear Ruta (Google Maps)
9. Crear Referencia de Camión
10. Asignar Camión a Tramo
11. Iniciar Tramo
12. Finalizar Tramo (calcula costo)
13. Finalizar Solicitud (costo total)

## Requisitos Previos

- Keycloak corriendo en puerto 8180
- API Gateway corriendo en puerto 8080
- Servicio Flota corriendo en puerto 8081
- Servicio Operaciones corriendo en puerto 8082
- Google Maps API Key configurada

## Credenciales de Prueba

**Usuario Admin:**
- Username: `admin`
- Password: `admin123`
- Roles: ADMIN, FLOTA_MANAGER, OPERACIONES_MANAGER

## Tests Automáticos

Cada request incluye tests que validan:
- Códigos de respuesta HTTP
- Estructura de datos
- Valores calculados (costos, distancias, tiempos)
- Transiciones de estado

Los tests se ejecutan automáticamente y muestran ✅ o ❌ en los resultados.

## Troubleshooting

**Error 401 Unauthorized:**
- Ejecutar primero el paso 1 (Login) para obtener un token válido

**Error 404 Not Found:**
- Verificar que todos los servicios estén corriendo
- Revisar que las URLs en las variables sean correctas

**Error 400 Bad Request:**
- Verificar que los pasos previos se hayan ejecutado correctamente
- Revisar que las variables se hayan guardado (solicitud_id, ruta_id, etc.)

**Flujo E2E falla en paso X:**
- Reiniciar los servicios para limpiar las bases de datos H2
- Ejecutar el flujo completo desde el paso 1
