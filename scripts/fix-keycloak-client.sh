#!/bin/bash

echo "Habilitando Direct Access Grants en postman-client..."

# Obtener token admin
TOKEN=$(curl -s -X POST "http://localhost:8180/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r '.access_token')

# Obtener ID del cliente postman
CLIENT_ID=$(curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8180/admin/realms/tpi-backend/clients" | \
  jq -r '.[] | select(.clientId=="postman-client") | .id')

echo "Client ID: $CLIENT_ID"

# Actualizar cliente con los campos necesarios
curl -X PUT \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  "http://localhost:8180/admin/realms/tpi-backend/clients/$CLIENT_ID" \
  -d '{
    "clientId": "postman-client",
    "enabled": true,
    "publicClient": true,
    "directAccessGrantsEnabled": true,
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": false,
    "authorizationServicesEnabled": false,
    "protocol": "openid-connect",
    "redirectUris": ["*"],
    "webOrigins": ["*"]
  }'

echo ""
echo "Cliente actualizado. Probando..."

sleep 2

curl -s -X POST "http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=postman-client" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r 'if .access_token then "✓ ¡TOKEN OBTENIDO!" else . end'
