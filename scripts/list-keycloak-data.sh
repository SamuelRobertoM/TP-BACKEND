#!/bin/bash

# Obtener token admin
TOKEN=$(curl -s -X POST "http://localhost:8180/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin123" | jq -r '.access_token')

echo "Usuarios en realm tpi-backend:"
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8180/admin/realms/tpi-backend/users" | jq -r '.[].username'

echo ""
echo "Clientes en realm tpi-backend:"
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8180/admin/realms/tpi-backend/clients" | jq -r '.[] | select(.clientId != null) | .clientId'
