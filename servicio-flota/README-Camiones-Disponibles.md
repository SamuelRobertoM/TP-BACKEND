# 🚛 Endpoint GET /api/camiones/disponibles

## ✅ **Implementación Completada**

Se ha implementado exitosamente el endpoint `GET /api/camiones/disponibles` en el microservicio `servicio-flota` usando **Spring Data JPA Specifications** para consultas dinámicas.

---

## 🏗️ **Cambios Realizados**

### 1. **CamionRepository.java**
- ✅ Extendido para usar `JpaSpecificationExecutor<Camion>`
- ✅ Habilitado el API de Criteria para búsquedas dinámicas

```java
public interface CamionRepository extends JpaRepository<Camion, Long>, JpaSpecificationExecutor<Camion> {
    // Ahora tenemos acceso a findAll(Specification<Camion> spec)
}
```

### 2. **CamionService.java**
- ✅ Importada `org.springframework.data.jpa.domain.Specification`
- ✅ Implementado método `buscarDisponibles(Double pesoMinimo, Double volumenMinimo)`
- ✅ Lógica de filtrado dinámico usando Specifications

```java
public List<Camion> buscarDisponibles(Double pesoMinimo, Double volumenMinimo) {
    // Filtro base: disponible = true
    Specification<Camion> spec = (root, query, cb) -> 
        cb.isTrue(root.get("disponible"));

    // Filtros opcionales dinámicos
    if (pesoMinimo != null) {
        spec = spec.and((root, query, cb) -> 
            cb.greaterThanOrEqualTo(root.get("capacidadPeso"), pesoMinimo));
    }

    if (volumenMinimo != null) {
        spec = spec.and((root, query, cb) -> 
            cb.greaterThanOrEqualTo(root.get("capacidadVolumen"), volumenMinimo));
    }

    return camionRepository.findAll(spec);
}
```

### 3. **CamionController.java**
- ✅ Implementado endpoint `GET /camiones/disponibles`
- ✅ Parámetros opcionales con `@RequestParam(required = false)`
- ✅ Documentación del endpoint

```java
@GetMapping("/disponibles")
public ResponseEntity<List<Camion>> obtenerCamionesDisponibles(
        @RequestParam(required = false) Double pesoMinimo,
        @RequestParam(required = false) Double volumenMinimo) {

    List<Camion> camionesDisponibles = camionService.buscarDisponibles(pesoMinimo, volumenMinimo);
    return ResponseEntity.ok(camionesDisponibles);
}
```

---

## 🔗 **Uso del Endpoint**

### **Endpoint**: `GET /api/camiones/disponibles`

### **Parámetros de consulta (opcionales)**:
- `pesoMinimo` (Double): Capacidad mínima de peso requerida en kg
- `volumenMinimo` (Double): Capacidad mínima de volumen requerida en m³

### **Respuesta**: 
- `200 OK`: Lista de camiones que cumplen los criterios
- Contenido: `List<Camion>`

---

## 📝 **Ejemplos de Uso**

### **1. Obtener todos los camiones disponibles**
```bash
GET /api/camiones/disponibles
```
**Resultado**: Todos los camiones con `disponible = true`

### **2. Filtrar por peso mínimo**
```bash
GET /api/camiones/disponibles?pesoMinimo=5000
```
**Resultado**: Camiones disponibles con capacidad ≥ 5000 kg

### **3. Filtrar por volumen mínimo**
```bash
GET /api/camiones/disponibles?volumenMinimo=50
```
**Resultado**: Camiones disponibles con capacidad ≥ 50 m³

### **4. Filtrar por peso Y volumen mínimo**
```bash
GET /api/camiones/disponibles?pesoMinimo=8000&volumenMinimo=40
```
**Resultado**: Camiones disponibles con:
- Capacidad de peso ≥ 8000 kg **Y**
- Capacidad de volumen ≥ 40 m³

### **5. Ejemplo con curl**
```bash
curl -X GET "http://localhost:8081/api/camiones/disponibles?pesoMinimo=6000&volumenMinimo=35" \
     -H "Content-Type: application/json"
```

---

## 🔍 **Lógica de Filtrado**

La consulta se construye dinámicamente usando **Spring Data JPA Specifications**:

1. **Filtro base obligatorio**: `disponible = true`
2. **Filtros opcionales que se agregan si se proporcionan**:
   - `capacidadPeso >= pesoMinimo`
   - `capacidadVolumen >= volumenMinimo`

### **Consulta SQL generada (ejemplo)**:
```sql
SELECT * FROM camiones 
WHERE disponible = true 
  AND capacidad_peso >= ?1 
  AND capacidad_volumen >= ?2
```

---

## ✅ **Ventajas de la Implementación**

### **1. Consultas Dinámicas**
- Los filtros se aplican solo si se proporcionan
- No hay consultas innecesarias con parámetros nulos

### **2. Performance**
- Una sola consulta a la base de datos
- Filtrado eficiente en el nivel de BD

### **3. Flexibilidad**
- Fácil extensión para agregar más filtros
- API limpia y entendible

### **4. Specifications Pattern**
- Reutilizable y componible
- Facilita testing unitario
- Seguimiento de mejores prácticas de Spring Data

---

## 🚀 **Estado de Compilación y Pruebas**

- ✅ **Compilación exitosa**: `mvn clean compile`
- ✅ **Pruebas pasadas**: `mvn test`  
- ✅ **Sin advertencias**: Sintaxis moderna de Specifications
- ✅ **Contexto Spring**: Carga correctamente todas las dependencias

---

## 📋 **Próximos Pasos Sugeridos**

1. ✅ **Completado**: Endpoint GET /camiones/disponibles implementado
2. 🔄 **Sugerido**: Agregar más filtros (marca, modelo, año, etc.)
3. 🔄 **Sugerido**: Implementar paginación para grandes volúmenes de datos
4. 🔄 **Sugerido**: Agregar ordenamiento por diferentes criterios
5. 🔄 **Sugerido**: Crear pruebas unitarias específicas para el nuevo endpoint

---

## 📄 **Actualización del Documento de Diseño**

El documento `Endpoints-Documentacion.md` debe ser actualizado:

**Estado anterior**: 🟡 Pendiente (Lógica)  
**Estado nuevo**: ✅ Implementado

---

**✅ Endpoint GET /api/camiones/disponibles implementado exitosamente con filtros dinámicos usando Spring Data JPA Specifications**