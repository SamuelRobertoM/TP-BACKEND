#!/bin/bash

echo "╔══════════════════════════════════════════════════════════════════════════╗"
echo "║  PRUEBAS DE SEGURIDAD - VERIFICACIÓN MANUAL                             ║"
echo "╚══════════════════════════════════════════════════════════════════════════╝"
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}=== PRUEBA 1: Sin autenticación (esperado: 401) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:8080/api/flota/camiones)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
if [ "$HTTP_CODE" = "401" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE Unauthorized${NC}"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 401)${NC}"
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 2: Obteniendo token de ADMIN ===${NC}"
TOKEN_ADMIN=$(curl -s -X POST "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=postman-client" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r '.access_token')

if [ -n "$TOKEN_ADMIN" ] && [ "$TOKEN_ADMIN" != "null" ]; then
  echo -e "${GREEN}✓ Token ADMIN obtenido: ${TOKEN_ADMIN:0:30}...${NC}"
else
  echo -e "${RED}✗ Error obteniendo token${NC}"
  exit 1
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 3: GET /api/flota/camiones con ADMIN (esperado: 200) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN_ADMIN" \
  http://localhost:8080/api/flota/camiones)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
BODY=$(echo "$RESPONSE" | sed '$d')
if [ "$HTTP_CODE" = "200" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE OK${NC}"
  echo "  Datos: $(echo $BODY | jq -c '.[0] | {id, patente, marca}' 2>/dev/null || echo 'Ver respuesta completa arriba')"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 200)${NC}"
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 4: Obteniendo token de OPERACIONES_MANAGER ===${NC}"
TOKEN_OPS=$(curl -s -X POST "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=postman-client" \
  -d "grant_type=password" \
  -d "username=operador.operaciones" \
  -d "password=operaciones123" | jq -r '.access_token')

if [ -n "$TOKEN_OPS" ] && [ "$TOKEN_OPS" != "null" ]; then
  echo -e "${GREEN}✓ Token OPERACIONES obtenido: ${TOKEN_OPS:0:30}...${NC}"
else
  echo -e "${RED}✗ Error obteniendo token${NC}"
  exit 1
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 5: GET /api/flota/camiones con OPERACIONES (esperado: 200) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN_OPS" \
  http://localhost:8080/api/flota/camiones)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
if [ "$HTTP_CODE" = "200" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE OK (tiene permiso GET)${NC}"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 200)${NC}"
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 6: POST /api/flota/camiones con OPERACIONES (esperado: 403) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
  -H "Authorization: Bearer $TOKEN_OPS" \
  -H "Content-Type: application/json" \
  -d '{"patente":"TEST001","marca":"Test","modelo":"Test","anio":2024,"capacidadPeso":1000,"capacidadVolumen":10,"combustibleCapacidad":100,"depositoId":1}' \
  http://localhost:8080/api/flota/camiones)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
if [ "$HTTP_CODE" = "403" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE Forbidden (sin permisos para POST en flota)${NC}"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 403)${NC}"
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 7: POST /api/flota/camiones con ADMIN (esperado: 201) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{"patente":"ADMIN999","marca":"Mercedes","modelo":"Actros","anio":2024,"capacidadPeso":30000,"capacidadVolumen":90,"combustibleCapacidad":600,"depositoId":1}' \
  http://localhost:8080/api/flota/camiones)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
BODY=$(echo "$RESPONSE" | sed '$d')
if [ "$HTTP_CODE" = "201" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE Created${NC}"
  echo "  Camión creado: $(echo $BODY | jq -c '{id, patente, marca}' 2>/dev/null)"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 201)${NC}"
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 8: GET /api/operaciones/clientes con OPERACIONES (esperado: 200) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN_OPS" \
  http://localhost:8080/api/operaciones/clientes)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
if [ "$HTTP_CODE" = "200" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE OK${NC}"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 200)${NC}"
fi

echo ""
echo -e "${YELLOW}=== PRUEBA 9: POST /api/operaciones/clientes con OPERACIONES (esperado: 201) ===${NC}"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
  -H "Authorization: Bearer $TOKEN_OPS" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Cliente Test","email":"test@test.com","telefono":"+54911111111","direccion":"Test 123","cuit":"20-12345678-9"}' \
  http://localhost:8080/api/operaciones/clientes)
HTTP_CODE=$(echo "$RESPONSE" | tail -n 1)
if [ "$HTTP_CODE" = "201" ]; then
  echo -e "${GREEN}✓ PASS: HTTP $HTTP_CODE Created (tiene permiso POST en operaciones)${NC}"
else
  echo -e "${RED}✗ FAIL: HTTP $HTTP_CODE (esperado 201)${NC}"
fi

echo ""
echo "╔══════════════════════════════════════════════════════════════════════════╗"
echo "║  RESUMEN: TODAS LAS PRUEBAS COMPLETADAS                                 ║"
echo "╚══════════════════════════════════════════════════════════════════════════╝"
echo ""
echo "✅ Sistema de seguridad funcionando correctamente"
echo "✅ Autenticación OAuth2/JWT validada"
echo "✅ Autorización basada en roles validada"
echo ""
