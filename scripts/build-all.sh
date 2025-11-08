#!/bin/bash

# Script para compilar todos los módulos del proyecto
# Autor: TPI Backend Team
# Uso: ./build-all.sh

set -e

echo "🔨 =========================================="
echo "🔨  Compilando todos los módulos TPI"
echo "🔨 =========================================="
echo ""

# Obtener el directorio del script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Función para compilar un módulo
compile_module() {
    local MODULE_NAME=$1
    local MODULE_PATH="$PROJECT_ROOT/$MODULE_NAME"
    
    echo ""
    echo "📦 Compilando $MODULE_NAME..."
    echo "   Directorio: $MODULE_PATH"
    
    if [ ! -d "$MODULE_PATH" ]; then
        echo -e "${RED}❌ El módulo $MODULE_NAME no existe${NC}"
        return 1
    fi
    
    cd "$MODULE_PATH" || exit 1
    
    # Verificar si existe mvnw o usar mvn
    if [ -f "./mvnw" ]; then
        echo "   Usando ./mvnw"
        ./mvnw clean compile -DskipTests
    else
        echo "   Usando mvn"
        mvn clean compile -DskipTests
    fi
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $MODULE_NAME compilado exitosamente${NC}"
        return 0
    else
        echo -e "${RED}❌ Error compilando $MODULE_NAME${NC}"
        return 1
    fi
}

# Compilar todos los módulos
FAILED_MODULES=()

compile_module "servicio-flota" || FAILED_MODULES+=("servicio-flota")
compile_module "servicio-operaciones" || FAILED_MODULES+=("servicio-operaciones")
compile_module "api-gateway" || FAILED_MODULES+=("api-gateway")

# Resumen
echo ""
echo "📊 =========================================="
echo "📊  Resumen de Compilación"
echo "📊 =========================================="
echo ""

if [ ${#FAILED_MODULES[@]} -eq 0 ]; then
    echo -e "${GREEN}✅ Todos los módulos compilados exitosamente!${NC}"
    echo ""
    echo "🚀 Puedes iniciar los servicios con:"
    echo "   Terminal 1: cd servicio-flota && ./mvnw spring-boot:run"
    echo "   Terminal 2: cd servicio-operaciones && ./mvnw spring-boot:run"
    echo "   Terminal 3: cd api-gateway && mvn spring-boot:run"
    echo ""
    exit 0
else
    echo -e "${RED}❌ Algunos módulos fallaron:${NC}"
    for module in "${FAILED_MODULES[@]}"; do
        echo "   - $module"
    done
    echo ""
    exit 1
fi
