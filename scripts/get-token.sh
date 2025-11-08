#!/bin/bash

# Script para obtener tokens JWT de Keycloak
# Uso: ./scripts/get-token.sh [admin|flota|operaciones|transportista|cliente]

KEYCLOAK_URL="http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token"
CLIENT_ID="postman-client"

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo -e "${BLUE}╔══════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   🔐 Generador de Tokens JWT - TPI      ║${NC}"
echo -e "${BLUE}╔══════════════════════════════════════════╗${NC}"
echo ""

# Determinar usuario
USUARIO=${1:-admin}

case $USUARIO in
  admin)
    USERNAME="admin"
    PASSWORD="admin123"
    ROLES="ADMIN, FLOTA_MANAGER"
    ;;
  flota)
    USERNAME="operador.flota"
    PASSWORD="flota123"
    ROLES="FLOTA_MANAGER"
    ;;
  operaciones)
    USERNAME="operador.operaciones"
    PASSWORD="operaciones123"
    ROLES="OPERACIONES_MANAGER"
    ;;
  transportista)
    USERNAME="transportista1"
    PASSWORD="transportista123"
    ROLES="TRANSPORTISTA, USER"
    ;;
  cliente)
    USERNAME="cliente.demo"
    PASSWORD="demo123"
    ROLES="USER"
    ;;
  *)
    echo -e "${YELLOW}⚠️  Usuario desconocido: $USUARIO${NC}"
    echo ""
    echo "Usuarios disponibles:"
    echo "  - admin          (ADMIN, FLOTA_MANAGER)"
    echo "  - flota          (FLOTA_MANAGER)"
    echo "  - operaciones    (OPERACIONES_MANAGER)"
    echo "  - transportista  (TRANSPORTISTA, USER)"
    echo "  - cliente        (USER)"
    echo ""
    echo "Uso: $0 [usuario]"
    echo "Ejemplo: $0 admin"
    exit 1
    ;;
esac

echo -e "${YELLOW}👤 Usuario:${NC} $USERNAME"
echo -e "${YELLOW}🎭 Roles:${NC} $ROLES"
echo ""

# Obtener token
echo -e "${BLUE}🔄 Solicitando token a Keycloak...${NC}"
RESPONSE=$(curl -s -X POST "$KEYCLOAK_URL" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=$CLIENT_ID" \
  -d "username=$USERNAME" \
  -d "password=$PASSWORD" \
  -d "grant_type=password")

# Verificar si hubo error
if echo "$RESPONSE" | jq -e '.error' > /dev/null 2>&1; then
  echo ""
  echo -e "${YELLOW}❌ Error al obtener el token:${NC}"
  echo "$RESPONSE" | jq '.'
  exit 1
fi

# Extraer token
TOKEN=$(echo "$RESPONSE" | jq -r '.access_token')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo ""
  echo -e "${YELLOW}❌ No se pudo obtener el token${NC}"
  echo "Respuesta:"
  echo "$RESPONSE" | jq '.'
  exit 1
fi

# Mostrar información del token
EXPIRES_IN=$(echo "$RESPONSE" | jq -r '.expires_in')
EXPIRES_MIN=$((EXPIRES_IN / 60))

echo -e "${GREEN}✅ Token obtenido exitosamente!${NC}"
echo ""
echo -e "${YELLOW}⏱️  Expira en:${NC} $EXPIRES_MIN minutos"
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}🔑 TOKEN JWT:${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "$TOKEN"
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${YELLOW}📋 Pasos para usar en Swagger:${NC}"
echo ""
echo "1. Abre Swagger UI: ${GREEN}http://localhost:8080/swagger-ui.html${NC}"
echo "2. Haz clic en el botón ${GREEN}'Authorize'${NC} 🔓"
echo "3. Pega el token de arriba en el campo 'Value'"
echo "4. Haz clic en ${GREEN}'Authorize'${NC}"
echo "5. ¡Cierra el diálogo y prueba los endpoints!"
echo ""
echo -e "${YELLOW}💡 Ejemplo de uso con curl:${NC}"
echo ""
echo "curl -H \"Authorization: Bearer $TOKEN\" \\"
echo "     http://localhost:8080/api/flota/camiones"
echo ""

# Guardar token en archivo temporal (opcional)
echo "$TOKEN" > /tmp/jwt-token-$USERNAME.txt
echo -e "${BLUE}💾 Token guardado en: /tmp/jwt-token-$USERNAME.txt${NC}"
echo ""
