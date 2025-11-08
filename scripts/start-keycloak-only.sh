#!/bin/bash

# Script para iniciar solo Keycloak en Docker
# Autor: TPI Backend Team
# Uso: ./start-keycloak-only.sh

set -e

echo "🔐 =========================================="
echo "🔐  Iniciando Keycloak en Docker"
echo "🔐 =========================================="
echo ""

# Obtener el directorio del script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo"
    echo "   Por favor inicia Docker y vuelve a intentar"
    exit 1
fi

echo "✅ Docker está corriendo"
echo ""

# Cambiar al directorio docker
cd "$PROJECT_ROOT/docker" || exit 1

# Parar contenedores existentes si están corriendo
echo "🛑 Deteniendo contenedores existentes (si los hay)..."
docker-compose -f docker-compose-keycloak.yml down -v 2>/dev/null || true
echo ""

# Levantar Keycloak
echo "🚀 Levantando Keycloak y PostgreSQL..."
docker-compose -f docker-compose-keycloak.yml up -d

# Esperar que Keycloak esté listo
echo ""
echo "⏳ Esperando que Keycloak esté listo..."
echo "   Esto puede tomar 1-2 minutos..."
echo ""

MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s -f http://localhost:8180/health/ready > /dev/null 2>&1; then
        echo ""
        echo "✅ =========================================="
        echo "✅  Keycloak está funcionando correctamente!"
        echo "✅ =========================================="
        echo ""
        echo "📋 Información de acceso:"
        echo "   🌐 URL Admin: http://localhost:8180"
        echo "   👤 Usuario:   admin"
        echo "   🔑 Password:  admin123"
        echo ""
        echo "📋 Realm configurado: tpi-backend"
        echo ""
        echo "👥 Usuarios de prueba disponibles:"
        echo "   - admin / admin123 (Todos los roles)"
        echo "   - operador.flota / flota123 (FLOTA_MANAGER)"
        echo "   - operador.operaciones / operaciones123 (OPERACIONES_MANAGER)"
        echo "   - transportista1 / trans123 (TRANSPORTISTA)"
        echo "   - cliente.demo / cliente123 (USER)"
        echo ""
        echo "🔑 Para obtener un token JWT, ejecuta:"
        echo ""
        echo "curl -X POST 'http://localhost:8180/realms/tpi-backend/protocol/openid-connect/token' \\"
        echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
        echo "  -d 'username=admin&password=admin123&grant_type=password&client_id=tpi-api-gateway&client_secret=tpi-gateway-secret-2024-secure'"
        echo ""
        echo "🎉 Listo para usar!"
        exit 0
    fi
    
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 10
done

echo ""
echo "❌ Keycloak no pudo iniciarse correctamente después de $((MAX_ATTEMPTS * 10)) segundos"
echo "   Revisa los logs con: docker-compose -f docker-compose-keycloak.yml logs"
exit 1
