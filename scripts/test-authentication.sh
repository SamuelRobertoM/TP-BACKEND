#!/bin/bash

# Script para testing completo del sistema con autenticación
# Autor: TPI Backend Team
# Uso: ./test-authentication.sh

set -e

echo "🧪 =========================================="
echo "🧪  Testing Sistema TPI con Keycloak"
echo "🧪 =========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar que Keycloak esté corriendo
echo "🔍 Verificando Keycloak..."
if ! curl -s -f http://localhost:8180/health/ready > /dev/null 2>&1; then
    echo -e "${RED}❌ Keycloak no está corriendo${NC}"
    echo "   Ejecuta: ./start-keycloak-only.sh"
    exit 1
fi
echo -e "${GREEN}✅ Keycloak está funcionando${NC}"
echo ""

# 1. Obtener token de autenticación
echo "🔑 Obteniendo token de autenticación para usuario 'admin'..."
TOKEN_RESPONSE=$(curl -s -X POST \
  "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin123&grant_type=password&client_id=tpi-api-gateway&client_secret=tpi-gateway-secret-2024-secure")

TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token' 2>/dev/null)

if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
    echo -e "${RED}❌ Error obteniendo token${NC}"
    echo "   Respuesta: $TOKEN_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✅ Token obtenido exitosamente${NC}"
echo "   Token (primeros 50 chars): ${TOKEN:0:50}..."
echo ""

# Función para testear un endpoint
test_endpoint() {
    local METHOD=$1
    local URL=$2
    local DESCRIPTION=$3
    local EXPECTED_STATUS=$4
    
    echo "📡 Testing: $DESCRIPTION"
    echo "   $METHOD $URL"
    
    RESPONSE=$(curl -s -w "\n%{http_code}" -X "$METHOD" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      "$URL" 2>/dev/null)
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "$EXPECTED_STATUS" ]; then
        echo -e "${GREEN}   ✅ Status: $HTTP_CODE (esperado)${NC}"
    else
        echo -e "${YELLOW}   ⚠️  Status: $HTTP_CODE (esperado: $EXPECTED_STATUS)${NC}"
    fi
    echo ""
}

# 2. Verificar servicios
echo "🔍 Verificando servicios..."
echo ""

# Servicio Flota
if curl -s -f "http://localhost:8081/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Servicio Flota funcionando (puerto 8081)${NC}"
    FLOTA_RUNNING=true
else
    echo -e "${YELLOW}⚠️  Servicio Flota no está corriendo (puerto 8081)${NC}"
    FLOTA_RUNNING=false
fi

# Servicio Operaciones
if curl -s -f "http://localhost:8082/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Servicio Operaciones funcionando (puerto 8082)${NC}"
    OPERACIONES_RUNNING=true
else
    echo -e "${YELLOW}⚠️  Servicio Operaciones no está corriendo (puerto 8082)${NC}"
    OPERACIONES_RUNNING=false
fi

# API Gateway
if curl -s -f "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ API Gateway funcionando (puerto 8080)${NC}"
    GATEWAY_RUNNING=true
else
    echo -e "${YELLOW}⚠️  API Gateway no está corriendo (puerto 8080)${NC}"
    GATEWAY_RUNNING=false
fi

echo ""

# 3. Testing de endpoints si los servicios están corriendo
if [ "$GATEWAY_RUNNING" = true ]; then
    echo "🧪 Testing endpoints a través del API Gateway..."
    echo ""
    
    # Test sin autenticación (debería fallar)
    echo "🔒 Testing acceso sin autenticación (debería fallar con 401)..."
    UNAUTH_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null "http://localhost:8080/api/flota/camiones")
    if [ "$UNAUTH_RESPONSE" = "401" ]; then
        echo -e "${GREEN}   ✅ Seguridad funcionando - acceso denegado sin token${NC}"
    else
        echo -e "${YELLOW}   ⚠️  Respuesta inesperada: $UNAUTH_RESPONSE${NC}"
    fi
    echo ""
    
    # Test con autenticación
    if [ "$FLOTA_RUNNING" = true ]; then
        test_endpoint "GET" "http://localhost:8080/api/flota/camiones" "Listar camiones (Flota)" "200"
        test_endpoint "GET" "http://localhost:8080/api/flota/depositos" "Listar depósitos (Flota)" "200"
    fi
    
    if [ "$OPERACIONES_RUNNING" = true ]; then
        test_endpoint "GET" "http://localhost:8080/api/operaciones/solicitudes" "Listar solicitudes (Operaciones)" "200"
        test_endpoint "GET" "http://localhost:8080/api/operaciones/clientes" "Listar clientes (Operaciones)" "200"
    fi
fi

# 4. Testing directo a microservicios
if [ "$FLOTA_RUNNING" = true ]; then
    echo "🔐 Testing directo a Servicio Flota (con JWT)..."
    test_endpoint "GET" "http://localhost:8081/api/camiones" "Acceso directo a Flota" "200"
fi

if [ "$OPERACIONES_RUNNING" = true ]; then
    echo "🔐 Testing directo a Servicio Operaciones (con JWT)..."
    test_endpoint "GET" "http://localhost:8082/api/solicitudes" "Acceso directo a Operaciones" "200"
fi

# Resumen
echo ""
echo "📊 =========================================="
echo "📊  Resumen del Testing"
echo "📊 =========================================="
echo ""
echo "Servicios:"
echo "   Keycloak:           ${GREEN}✅ Funcionando${NC}"
[ "$GATEWAY_RUNNING" = true ] && echo -e "   API Gateway:        ${GREEN}✅ Funcionando${NC}" || echo -e "   API Gateway:        ${YELLOW}⚠️  No disponible${NC}"
[ "$FLOTA_RUNNING" = true ] && echo -e "   Servicio Flota:     ${GREEN}✅ Funcionando${NC}" || echo -e "   Servicio Flota:     ${YELLOW}⚠️  No disponible${NC}"
[ "$OPERACIONES_RUNNING" = true ] && echo -e "   Servicio Operaciones: ${GREEN}✅ Funcionando${NC}" || echo -e "   Servicio Operaciones: ${YELLOW}⚠️  No disponible${NC}"
echo ""
echo "💡 Para levantar los servicios manualmente:"
echo "   cd servicio-flota && ./mvnw spring-boot:run"
echo "   cd servicio-operaciones && ./mvnw spring-boot:run"
echo "   cd api-gateway && mvn spring-boot:run"
echo ""
echo "🎉 Testing completado!"
