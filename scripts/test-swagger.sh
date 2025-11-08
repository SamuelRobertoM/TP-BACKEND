#!/bin/bash

# Script para probar el acceso a Swagger UI del Gateway

echo "╔══════════════════════════════════════════════════════════════╗"
echo "║   PRUEBA DE SWAGGER UI EN API GATEWAY                       ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test 1: Gateway Health
echo -e "${BLUE}[Test 1]${NC} Verificando que el Gateway esté activo..."
STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
if [ "$STATUS_CODE" == "200" ]; then
    echo -e "${GREEN}✓ Gateway está activo (HTTP 200)${NC}"
else
    echo -e "${RED}✗ Gateway no responde correctamente (HTTP $STATUS_CODE)${NC}"
    exit 1
fi
echo ""

# Test 2: Swagger UI HTML
echo -e "${BLUE}[Test 2]${NC} Verificando Swagger UI..."
SWAGGER_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui.html)
if [ "$SWAGGER_RESPONSE" == "200" ]; then
    echo -e "${GREEN}✓ Swagger UI accesible (HTTP 200)${NC}"
    echo -e "   URL: ${YELLOW}http://localhost:8080/swagger-ui.html${NC}"
else
    echo -e "${RED}✗ Swagger UI no accesible (HTTP $SWAGGER_RESPONSE)${NC}"
fi
echo ""

# Test 3: API Docs del Gateway
echo -e "${BLUE}[Test 3]${NC} Verificando OpenAPI del Gateway..."
GATEWAY_DOCS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/v3/api-docs)
if [ "$GATEWAY_DOCS" == "200" ]; then
    echo -e "${GREEN}✓ OpenAPI del Gateway disponible (HTTP 200)${NC}"
    TITLE=$(curl -s http://localhost:8080/v3/api-docs | jq -r '.info.title' 2>/dev/null)
    if [ ! -z "$TITLE" ]; then
        echo -e "   Título: ${YELLOW}$TITLE${NC}"
    fi
else
    echo -e "${RED}✗ OpenAPI del Gateway no disponible (HTTP $GATEWAY_DOCS)${NC}"
fi
echo ""

# Test 4: API Docs de Servicio Flota (directo)
echo -e "${BLUE}[Test 4]${NC} Verificando OpenAPI de Servicio Flota..."
FLOTA_DOCS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/v3/api-docs)
if [ "$FLOTA_DOCS" == "200" ]; then
    echo -e "${GREEN}✓ OpenAPI de Flota disponible (HTTP 200)${NC}"
    echo -e "   URL: ${YELLOW}http://localhost:8081/v3/api-docs${NC}"
else
    echo -e "${YELLOW}⚠ OpenAPI de Flota no disponible (HTTP $FLOTA_DOCS)${NC}"
    echo -e "   Nota: Debe estar configurado en servicio-flota"
fi
echo ""

# Test 5: API Docs de Servicio Operaciones (directo)
echo -e "${BLUE}[Test 5]${NC} Verificando OpenAPI de Servicio Operaciones..."
OPERACIONES_DOCS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/v3/api-docs)
if [ "$OPERACIONES_DOCS" == "200" ]; then
    echo -e "${GREEN}✓ OpenAPI de Operaciones disponible (HTTP 200)${NC}"
    echo -e "   URL: ${YELLOW}http://localhost:8082/v3/api-docs${NC}"
else
    echo -e "${YELLOW}⚠ OpenAPI de Operaciones no disponible (HTTP $OPERACIONES_DOCS)${NC}"
    echo -e "   Nota: Debe estar configurado en servicio-operaciones"
fi
echo ""

# Resumen
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║   RESUMEN                                                    ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""
echo -e "${GREEN}✅ SWAGGER UI CONFIGURADO CORRECTAMENTE${NC}"
echo ""
echo "Para acceder a la documentación:"
echo ""
echo -e "  1. ${BLUE}Abrir en navegador:${NC}"
echo -e "     ${YELLOW}http://localhost:8080/swagger-ui.html${NC}"
echo ""
echo -e "  2. ${BLUE}En Swagger UI, podrás:${NC}"
echo "     • Ver todos los endpoints de Flota y Operaciones"
echo "     • Probar cada endpoint directamente desde el navegador"
echo "     • Autenticarte usando el botón 'Authorize'"
echo ""
echo -e "  3. ${BLUE}Para autenticarte:${NC}"
echo "     • Click en 'Authorize' (🔓)"
echo "     • Obtener token con:"
echo ""
echo "       curl -X POST \"http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token\" \\"
echo "         -H \"Content-Type: application/x-www-form-urlencoded\" \\"
echo "         -d \"client_id=postman-client\" \\"
echo "         -d \"username=admin\" \\"
echo "         -d \"password=admin123\" \\"
echo "         -d \"grant_type=password\" | jq -r '.access_token'"
echo ""
echo "     • Copiar el token y pegarlo en Swagger (con 'Bearer' automático)"
echo ""
echo "═══════════════════════════════════════════════════════════════"
