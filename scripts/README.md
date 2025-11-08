# README - Scripts de Automatización

Este directorio contiene scripts para facilitar el desarrollo y testing del proyecto TPI Backend.

## 📋 Scripts Disponibles

### � `start-all-services.sh` ⭐ **NUEVO**
Inicia todo el sistema completo: Keycloak + compilación de proyectos.

**Uso:**
```bash
./scripts/start-all-services.sh
```

**Qué hace:**
- ✅ Verifica prerrequisitos (Docker, Maven)
- ✅ Inicia Keycloak con PostgreSQL
- ✅ Compila servicio-flota
- ✅ Compila servicio-operaciones
- ✅ Compila api-gateway
- ✅ Muestra instrucciones para iniciar servicios Spring Boot

**Después de ejecutar:**
Abre 3 terminales e inicia cada servicio:
```bash
# Terminal 1
cd servicio-flota && ./mvnw spring-boot:run

# Terminal 2
cd servicio-operaciones && ./mvnw spring-boot:run

# Terminal 3
cd api-gateway && ./mvnw spring-boot:run
```

---

### �🔐 `start-keycloak-only.sh`
Inicia solo Keycloak en Docker con PostgreSQL.

**Uso:**
```bash
./scripts/start-keycloak-only.sh
```

**Qué hace:**
- Levanta Keycloak en el puerto 8180
- Levanta PostgreSQL en el puerto 5433
- Importa la configuración del realm automáticamente
- Verifica que Keycloak esté funcionando correctamente
- Muestra información de acceso y usuarios de prueba

**Acceso:**
- Admin Console: http://localhost:8180
- Usuario: `admin`
- Password: `admin123`

---

### 🔨 `build-all.sh`
Compila todos los módulos del proyecto.

**Uso:**
```bash
./scripts/build-all.sh
```

**Qué hace:**
- Compila servicio-flota
- Compila servicio-operaciones
- Compila api-gateway
- Muestra resumen de resultados

---

### 🧪 `test-endpoints-security.sh` ⭐ **NUEVO - Pruebas Completas**
Ejecuta pruebas exhaustivas de seguridad en TODOS los endpoints.

**Uso:**
```bash
./scripts/test-endpoints-security.sh
```

**Qué hace:**
- ✅ Obtiene tokens para ADMIN y OPERACIONES_MANAGER
- ✅ Prueba más de 50 endpoints diferentes
- ✅ Valida autenticación (401 sin token)
- ✅ Valida autorización (403 sin permisos)
- ✅ Valida acceso correcto (200/201 con permisos)
- ✅ Prueba ambos servicios (Flota y Operaciones)
- ✅ Genera reporte detallado con estadísticas

**Pruebas incluidas:**
- Servicio Flota: Camiones, Tarifas, Depósitos
- Servicio Operaciones: Clientes, Contenedores, Rutas, Tramos
- Códigos HTTP: 200, 201, 401, 403, 404
- Roles: ADMIN, OPERACIONES_MANAGER, TRANSPORTISTA

**Resultado esperado:**
```
Total de pruebas: 50+
Pruebas exitosas: 50+
Pruebas fallidas: 0
Tasa de éxito: 100%
¡TODAS LAS PRUEBAS PASARON! ✓
```

---

### 🧪 `test-authentication.sh`
Ejecuta tests básicos de autenticación y autorización.

**Uso:**
```bash
./scripts/test-authentication.sh
```

**Requisitos:**
- Keycloak debe estar corriendo (ejecutar `start-keycloak-only.sh` primero)
- Los servicios pueden estar corriendo o no (el script lo detecta)

**Qué hace:**
- Verifica que Keycloak esté funcionando
- Obtiene un token JWT de Keycloak
- Detecta qué servicios están corriendo
- Prueba autenticación sin token (debería fallar)
- Prueba autenticación con token (debería funcionar)
- Muestra resumen de resultados

---

## 🚀 Flujo de Trabajo Recomendado

### Para Desarrollo Local:

1. **Iniciar Keycloak:**
```bash
./scripts/start-keycloak-only.sh
```

2. **Compilar todos los módulos:**
```bash
./scripts/build-all.sh
```

3. **Iniciar servicios en terminales separadas:**

Terminal 1:
```bash
cd servicio-flota
./mvnw spring-boot:run
```

Terminal 2:
```bash
cd servicio-operaciones
./mvnw spring-boot:run
```

Terminal 3:
```bash
cd api-gateway
mvn spring-boot:run
```

4. **Probar el sistema:**
```bash
./scripts/test-authentication.sh
```

---

## 🔑 Usuarios de Prueba

Todos los usuarios están pre-configurados en Keycloak:

| Usuario | Password | Roles |
|---------|----------|-------|
| `admin` | `admin123` | ADMIN, FLOTA_MANAGER, OPERACIONES_MANAGER, USER |
| `operador.flota` | `flota123` | FLOTA_MANAGER, USER |
| `operador.operaciones` | `operaciones123` | OPERACIONES_MANAGER, USER |
| `transportista1` | `trans123` | TRANSPORTISTA, USER |
| `cliente.demo` | `cliente123` | USER |

---

## 📡 Obtener Token JWT Manualmente

```bash
curl -X POST 'http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=admin' \
  -d 'password=admin123' \
  -d 'grant_type=password' \
  -d 'client_id=tpi-api-gateway' \
  -d 'client_secret=tpi-gateway-secret-2024-secure'
```

---

## 🛑 Detener Keycloak

```bash
cd docker
docker-compose -f docker-compose-keycloak.yml down
```

Para eliminar también los datos:
```bash
docker-compose -f docker-compose-keycloak.yml down -v
```

---

## ❓ Troubleshooting

### Keycloak no inicia
- Verifica que Docker esté corriendo
- Verifica que el puerto 8180 no esté en uso
- Revisa los logs: `docker-compose -f docker/docker-compose-keycloak.yml logs`

### No puedo obtener token
- Verifica que Keycloak esté funcionando: `curl http://localhost:8180/health/ready`
- Verifica las credenciales del usuario
- Verifica el client_secret en la configuración

### Servicios no se comunican
- Verifica que todos los servicios estén corriendo
- Verifica los puertos: 8080 (gateway), 8081 (flota), 8082 (operaciones), 8180 (keycloak)
- Revisa los logs de cada servicio
