# Configuración de API Keys

## ⚠️ IMPORTANTE: SEGURIDAD DE API KEYS

Este proyecto utiliza **Google Maps Distance Matrix API** para cálculos de distancias y tiempos reales.

### 📋 Cómo configurar tu API Key

1. **Obtener una API Key de Google Maps:**
   - Ve a [Google Cloud Console](https://console.cloud.google.com/)
   - Crea un proyecto nuevo o selecciona uno existente
   - Habilita la API "Distance Matrix API"
   - Ve a "Credenciales" y crea una API Key
   - Restringe la API Key para uso solo en "Distance Matrix API"

2. **Configurar localmente:**
   ```bash
   cd servicio-operaciones/src/main/resources
   cp application.properties.example application.properties
   ```

3. **Editar `application.properties`:**
   ```properties
   google.maps.api.key=TU_API_KEY_AQUI
   ```

### 🔒 Seguridad

- **NUNCA** commitees `application.properties` con tu API key real
- El archivo `application.properties` está en `.gitignore` para prevenir commits accidentales
- Usa `application.properties.example` como plantilla (sin API key real)
- En producción, usa variables de entorno o servicios de gestión de secretos

### 📝 Archivos de configuración

| Archivo | Propósito | ¿Se commitea? |
|---------|-----------|---------------|
| `application.properties` | Configuración local con API key real | ❌ NO |
| `application.properties.example` | Plantilla sin API key | ✅ SÍ |

### 🚨 Si accidentalmente commiteaste tu API Key

1. **Rotar la API Key inmediatamente** en Google Cloud Console
2. Eliminar los commits que contienen la key del historial de Git
3. Forzar push al repositorio remoto

### 💡 Alternativas para producción

- Usar **Spring Cloud Config Server**
- Usar **Azure Key Vault** o **AWS Secrets Manager**
- Variables de entorno en el servidor/contenedor
