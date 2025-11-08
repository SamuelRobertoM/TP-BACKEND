#!/bin/bash

echo "Configurando clientes de Keycloak..."

# Obtener token admin
TOKEN=$(curl -s -X POST "http://localhost:8180/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r '.access_token')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "Error obteniendo token de administrador"
  exit 1
fi

echo "Token de admin obtenido ✓"

# Configurar postman-client
echo "Configurando postman-client..."
CLIENT_ID=$(curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8180/admin/realms/tpi-backend/clients" | \
  jq -r '.[] | select(.clientId=="postman-client") | .id')

if [ ! -z "$CLIENT_ID" ]; then
  curl -s -X GET \
    -H "Authorization: Bearer $TOKEN" \
    "http://localhost:8180/admin/realms/tpi-backend/clients/$CLIENT_ID" > /tmp/postman-config.json
  
  jq '.directAccessGrantsEnabled = true | .standardFlowEnabled = true | .implicitFlowEnabled = true' \
    /tmp/postman-config.json > /tmp/postman-updated.json
  
  curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    "http://localhost:8180/admin/realms/tpi-backend/clients/$CLIENT_ID" \
    -d @/tmp/postman-updated.json
  
  echo "postman-client configurado ✓"
fi

# Configurar tpi-api-gateway
echo "Configurando tpi-api-gateway..."
GATEWAY_ID=$(curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8180/admin/realms/tpi-backend/clients" | \
  jq -r '.[] | select(.clientId=="tpi-api-gateway") | .id')

if [ ! -z "$GATEWAY_ID" ]; then
  curl -s -X GET \
    -H "Authorization: Bearer $TOKEN" \
    "http://localhost:8180/admin/realms/tpi-backend/clients/$GATEWAY_ID" > /tmp/gateway-config.json
  
  jq '.directAccessGrantsEnabled = true | .serviceAccountsEnabled = true' \
    /tmp/gateway-config.json > /tmp/gateway-updated.json
  
  curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    "http://localhost:8180/admin/realms/tpi-backend/clients/$GATEWAY_ID" \
    -d @/tmp/gateway-updated.json
  
  echo "tpi-api-gateway configurado ✓"
fi

echo ""
echo "Probando autenticación con admin..."
curl -s -X POST "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=postman-client" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r 'if .access_token then "✓ Token obtenido correctamente" else .error end'
