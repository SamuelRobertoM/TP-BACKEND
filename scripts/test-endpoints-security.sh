#!/bin/bash

##############################################################################
# Script de Pruebas de Seguridad para Endpoints
# 
# Valida autenticación y autorización en todos los endpoints del sistema
# Prueba con dos usuarios: admin (rol ADMIN) y operaciones (rol OPERACIONES_MANAGER)
#
# Códigos HTTP esperados:
# - 401 Unauthorized: Sin token o token inválido
# - 403 Forbidden: Token válido pero sin permisos suficientes
# - 200/201 OK: Operación exitosa con permisos adecuados
# - 404 Not Found: Recurso no encontrado (con permisos válidos)
##############################################################################

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

# Configuración
KEYCLOAK_URL="http://localhost:8180"
GATEWAY_URL="http://localhost:8080"
REALM="tpi-backend"
CLIENT_ID="tpi-api-gateway"
CLIENT_SECRET="tpi-gateway-secret-2024"

# Contadores
TESTS_TOTAL=0
TESTS_PASSED=0
TESTS_FAILED=0

# Arrays para almacenar resultados
declare -a FAILED_TESTS

##############################################################################
# FUNCIONES AUXILIARES
##############################################################################

print_header() {
    echo -e "\n${BOLD}${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BOLD}${BLUE}  $1${NC}"
    echo -e "${BOLD}${BLUE}═══════════════════════════════════════════════════════════════${NC}\n"
}

print_section() {
    echo -e "\n${BOLD}${CYAN}▶ $1${NC}"
    echo -e "${CYAN}───────────────────────────────────────────────────────────────${NC}"
}

print_test() {
    echo -e "${YELLOW}Testing:${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓ PASS:${NC} $1"
    ((TESTS_PASSED++))
}

print_fail() {
    echo -e "${RED}✗ FAIL:${NC} $1"
    FAILED_TESTS+=("$1")
    ((TESTS_FAILED++))
}

print_info() {
    echo -e "${BLUE}ℹ INFO:${NC} $1"
}

# Función para obtener token de Keycloak
get_token() {
    local username=$1
    local password=$2
    
    local response=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "client_id=${CLIENT_ID}" \
        -d "client_secret=${CLIENT_SECRET}" \
        -d "grant_type=password" \
        -d "username=${username}" \
        -d "password=${password}")
    
    echo "$response" | jq -r '.access_token'
}

# Función para realizar prueba de endpoint
test_endpoint() {
    local test_name=$1
    local method=$2
    local endpoint=$3
    local token=$4
    local expected_code=$5
    local data=$6
    
    ((TESTS_TOTAL++))
    
    print_test "$test_name"
    
    local curl_cmd="curl -s -w '\n%{http_code}' -X ${method} '${GATEWAY_URL}${endpoint}'"
    
    if [ -n "$token" ]; then
        curl_cmd="${curl_cmd} -H 'Authorization: Bearer ${token}'"
    fi
    
    curl_cmd="${curl_cmd} -H 'Content-Type: application/json'"
    
    if [ -n "$data" ]; then
        curl_cmd="${curl_cmd} -d '${data}'"
    fi
    
    # Ejecutar curl y capturar respuesta y código
    local response=$(eval $curl_cmd)
    local http_code=$(echo "$response" | tail -n 1)
    local body=$(echo "$response" | sed '$d')
    
    # Verificar código HTTP
    if [ "$http_code" = "$expected_code" ]; then
        print_success "${method} ${endpoint} → HTTP ${http_code} (esperado: ${expected_code})"
        if [ -n "$body" ] && [ "$body" != "null" ]; then
            echo -e "  ${BLUE}Response:${NC} $(echo $body | jq -c '.' 2>/dev/null || echo $body | head -c 100)"
        fi
    else
        print_fail "${method} ${endpoint} → HTTP ${http_code} (esperado: ${expected_code})"
        echo -e "  ${RED}Response:${NC} $(echo $body | head -c 200)"
    fi
    
    # Pequeña pausa entre requests
    sleep 0.1
}

##############################################################################
# INICIO DE PRUEBAS
##############################################################################

print_header "PRUEBAS DE SEGURIDAD DE ENDPOINTS - TPI BACKEND"

echo -e "${BOLD}Configuración:${NC}"
echo -e "  Keycloak: ${KEYCLOAK_URL}"
echo -e "  Gateway:  ${GATEWAY_URL}"
echo -e "  Realm:    ${REALM}"

# Verificar que los servicios estén corriendo
print_section "Verificando Servicios"

if ! curl -s "${KEYCLOAK_URL}/health/ready" > /dev/null 2>&1; then
    echo -e "${RED}ERROR: Keycloak no está disponible en ${KEYCLOAK_URL}${NC}"
    echo "Por favor, inicia Keycloak con: ./scripts/start-keycloak-only.sh"
    exit 1
fi
print_info "Keycloak está disponible ✓"

if ! curl -s "${GATEWAY_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${YELLOW}ADVERTENCIA: API Gateway no responde en ${GATEWAY_URL}${NC}"
    echo "Asegúrate de que el Gateway esté corriendo"
fi

##############################################################################
# OBTENER TOKENS
##############################################################################

print_section "Obteniendo Tokens de Autenticación"

print_info "Obteniendo token para usuario: admin (rol ADMIN)"
ADMIN_TOKEN=$(get_token "admin" "admin123")
if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" = "null" ]; then
    echo -e "${RED}ERROR: No se pudo obtener token para admin${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Token admin obtenido (${#ADMIN_TOKEN} caracteres)${NC}"

print_info "Obteniendo token para usuario: operador.operaciones (rol OPERACIONES_MANAGER)"
OPERACIONES_TOKEN=$(get_token "operador.operaciones" "operaciones123")
if [ -z "$OPERACIONES_TOKEN" ] || [ "$OPERACIONES_TOKEN" = "null" ]; then
    echo -e "${RED}ERROR: No se pudo obtener token para operador.operaciones${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Token operaciones obtenido (${#OPERACIONES_TOKEN} caracteres)${NC}"

print_info "Obteniendo token para usuario: transportista1 (rol TRANSPORTISTA)"
TRANSPORTISTA_TOKEN=$(get_token "transportista1" "trans123")
if [ -z "$TRANSPORTISTA_TOKEN" ] || [ "$TRANSPORTISTA_TOKEN" = "null" ]; then
    echo -e "${YELLOW}ADVERTENCIA: No se pudo obtener token para transportista1${NC}"
fi

##############################################################################
# PRUEBAS DE ENDPOINTS - SERVICIO FLOTA
##############################################################################

print_header "PRUEBAS SERVICIO FLOTA - /api/flota/*"

# ============================================================================
# CAMIONES
# ============================================================================

print_section "Endpoint: GET /api/flota/camiones (Listar todos los camiones)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/flota/camiones" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/flota/camiones" \
    "$OPERACIONES_TOKEN" \
    "200"

test_endpoint \
    "Con rol ADMIN" \
    "GET" \
    "/api/flota/camiones" \
    "$ADMIN_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: GET /api/flota/camiones/{id} (Obtener camión por ID)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/flota/camiones/1" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/flota/camiones/1" \
    "$OPERACIONES_TOKEN" \
    "200"

test_endpoint \
    "Con rol ADMIN (ID inexistente)" \
    "GET" \
    "/api/flota/camiones/999" \
    "$ADMIN_TOKEN" \
    "404"

# ----------------------------------------------------------------------------

print_section "Endpoint: POST /api/flota/camiones (Crear camión)"

CAMION_DATA='{
  "patente": "TEST123",
  "marca": "Mercedes Benz",
  "modelo": "Actros 2041",
  "anio": 2023,
  "capacidadPeso": 25000.0,
  "capacidadVolumen": 80.0,
  "combustibleCapacidad": 500.0,
  "depositoId": 1
}'

test_endpoint \
    "Sin autenticación" \
    "POST" \
    "/api/flota/camiones" \
    "" \
    "401" \
    "$CAMION_DATA"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (sin permisos)" \
    "POST" \
    "/api/flota/camiones" \
    "$OPERACIONES_TOKEN" \
    "403" \
    "$CAMION_DATA"

test_endpoint \
    "Con rol ADMIN (con permisos)" \
    "POST" \
    "/api/flota/camiones" \
    "$ADMIN_TOKEN" \
    "201" \
    "$CAMION_DATA"

# ----------------------------------------------------------------------------

print_section "Endpoint: PUT /api/flota/camiones/{id} (Actualizar camión)"

CAMION_UPDATE_DATA='{
  "marca": "Scania",
  "modelo": "R450",
  "anio": 2024
}'

test_endpoint \
    "Sin autenticación" \
    "PUT" \
    "/api/flota/camiones/1" \
    "" \
    "401" \
    "$CAMION_UPDATE_DATA"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (sin permisos)" \
    "PUT" \
    "/api/flota/camiones/1" \
    "$OPERACIONES_TOKEN" \
    "403" \
    "$CAMION_UPDATE_DATA"

test_endpoint \
    "Con rol ADMIN (con permisos)" \
    "PUT" \
    "/api/flota/camiones/1" \
    "$ADMIN_TOKEN" \
    "200" \
    "$CAMION_UPDATE_DATA"

# ============================================================================
# TARIFAS
# ============================================================================

print_section "Endpoint: GET /api/flota/tarifas/actual (Obtener tarifa activa)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/flota/tarifas/actual" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/flota/tarifas/actual" \
    "$OPERACIONES_TOKEN" \
    "200"

test_endpoint \
    "Con rol ADMIN" \
    "GET" \
    "/api/flota/tarifas/actual" \
    "$ADMIN_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: GET /api/flota/tarifas (Listar todas las tarifas)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/flota/tarifas" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/flota/tarifas" \
    "$OPERACIONES_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: POST /api/flota/tarifas (Crear tarifa)"

TARIFA_DATA='{
  "costoKmLitro": 1.5,
  "costoHoraChofer": 350.0,
  "costoHoraCamion": 500.0
}'

test_endpoint \
    "Sin autenticación" \
    "POST" \
    "/api/flota/tarifas" \
    "" \
    "401" \
    "$TARIFA_DATA"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (sin permisos)" \
    "POST" \
    "/api/flota/tarifas" \
    "$OPERACIONES_TOKEN" \
    "403" \
    "$TARIFA_DATA"

test_endpoint \
    "Con rol ADMIN (con permisos)" \
    "POST" \
    "/api/flota/tarifas" \
    "$ADMIN_TOKEN" \
    "201" \
    "$TARIFA_DATA"

##############################################################################
# PRUEBAS DE ENDPOINTS - SERVICIO OPERACIONES
##############################################################################

print_header "PRUEBAS SERVICIO OPERACIONES - /api/operaciones/*"

# ============================================================================
# CLIENTES
# ============================================================================

print_section "Endpoint: GET /api/operaciones/clientes (Listar todos los clientes)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/operaciones/clientes" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/operaciones/clientes" \
    "$OPERACIONES_TOKEN" \
    "200"

test_endpoint \
    "Con rol ADMIN" \
    "GET" \
    "/api/operaciones/clientes" \
    "$ADMIN_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: GET /api/operaciones/clientes/{id} (Obtener cliente por ID)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/operaciones/clientes/1" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/operaciones/clientes/1" \
    "$OPERACIONES_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: POST /api/operaciones/clientes (Crear cliente)"

CLIENTE_DATA='{
  "nombre": "Cliente Test",
  "email": "test@example.com",
  "telefono": "+54 9 11 1234-5678",
  "direccion": "Av. Test 123",
  "cuit": "20-12345678-9"
}'

test_endpoint \
    "Sin autenticación" \
    "POST" \
    "/api/operaciones/clientes" \
    "" \
    "401" \
    "$CLIENTE_DATA"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (con permisos)" \
    "POST" \
    "/api/operaciones/clientes" \
    "$OPERACIONES_TOKEN" \
    "201" \
    "$CLIENTE_DATA"

# ----------------------------------------------------------------------------

print_section "Endpoint: PUT /api/operaciones/clientes/{id} (Actualizar cliente)"

CLIENTE_UPDATE_DATA='{
  "nombre": "Cliente Test Actualizado",
  "telefono": "+54 9 11 9876-5432"
}'

test_endpoint \
    "Sin autenticación" \
    "PUT" \
    "/api/operaciones/clientes/1" \
    "" \
    "401" \
    "$CLIENTE_UPDATE_DATA"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (con permisos)" \
    "PUT" \
    "/api/operaciones/clientes/1" \
    "$OPERACIONES_TOKEN" \
    "200" \
    "$CLIENTE_UPDATE_DATA"

# ----------------------------------------------------------------------------

print_section "Endpoint: DELETE /api/operaciones/clientes/{id} (Eliminar cliente)"

test_endpoint \
    "Sin autenticación" \
    "DELETE" \
    "/api/operaciones/clientes/999" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (sin permisos)" \
    "DELETE" \
    "/api/operaciones/clientes/999" \
    "$OPERACIONES_TOKEN" \
    "403"

test_endpoint \
    "Con rol ADMIN (con permisos, recurso no existe)" \
    "DELETE" \
    "/api/operaciones/clientes/999" \
    "$ADMIN_TOKEN" \
    "404"

# ============================================================================
# CONTENEDORES
# ============================================================================

print_section "Endpoint: GET /api/operaciones/contenedores (Listar contenedores)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/operaciones/contenedores" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/operaciones/contenedores" \
    "$OPERACIONES_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: POST /api/operaciones/contenedores (Crear contenedor)"

CONTENEDOR_DATA='{
  "numero": "CONT-TEST-001",
  "peso": 5000.0,
  "volumen": 25.0,
  "descripcion": "Contenedor de prueba"
}'

test_endpoint \
    "Sin autenticación" \
    "POST" \
    "/api/operaciones/contenedores" \
    "" \
    "401" \
    "$CONTENEDOR_DATA"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (con permisos)" \
    "POST" \
    "/api/operaciones/contenedores" \
    "$OPERACIONES_TOKEN" \
    "201" \
    "$CONTENEDOR_DATA"

# ============================================================================
# RUTAS
# ============================================================================

print_section "Endpoint: GET /api/operaciones/rutas (Listar rutas)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/operaciones/rutas" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/operaciones/rutas" \
    "$OPERACIONES_TOKEN" \
    "200"

test_endpoint \
    "Con rol ADMIN" \
    "GET" \
    "/api/operaciones/rutas" \
    "$ADMIN_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: GET /api/operaciones/rutas/{id} (Obtener ruta por ID)"

test_endpoint \
    "Sin autenticación" \
    "GET" \
    "/api/operaciones/rutas/1" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "GET" \
    "/api/operaciones/rutas/1" \
    "$OPERACIONES_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: DELETE /api/operaciones/rutas/{id} (Eliminar ruta)"

test_endpoint \
    "Sin autenticación" \
    "DELETE" \
    "/api/operaciones/rutas/999" \
    "" \
    "401"

test_endpoint \
    "Con rol OPERACIONES_MANAGER (sin permisos)" \
    "DELETE" \
    "/api/operaciones/rutas/999" \
    "$OPERACIONES_TOKEN" \
    "403"

test_endpoint \
    "Con rol ADMIN (con permisos)" \
    "DELETE" \
    "/api/operaciones/rutas/999" \
    "$ADMIN_TOKEN" \
    "404"

# ============================================================================
# TRAMOS (Endpoints especiales para TRANSPORTISTA)
# ============================================================================

print_section "Endpoint: POST /api/operaciones/tramos/{id}/iniciar (Iniciar tramo - TRANSPORTISTA)"

test_endpoint \
    "Sin autenticación" \
    "POST" \
    "/api/operaciones/tramos/1/iniciar" \
    "" \
    "401"

if [ -n "$TRANSPORTISTA_TOKEN" ] && [ "$TRANSPORTISTA_TOKEN" != "null" ]; then
    test_endpoint \
        "Con rol TRANSPORTISTA (con permisos especiales)" \
        "POST" \
        "/api/operaciones/tramos/1/iniciar" \
        "$TRANSPORTISTA_TOKEN" \
        "200"
fi

test_endpoint \
    "Con rol OPERACIONES_MANAGER" \
    "POST" \
    "/api/operaciones/tramos/1/iniciar" \
    "$OPERACIONES_TOKEN" \
    "200"

test_endpoint \
    "Con rol ADMIN" \
    "POST" \
    "/api/operaciones/tramos/1/iniciar" \
    "$ADMIN_TOKEN" \
    "200"

# ----------------------------------------------------------------------------

print_section "Endpoint: POST /api/operaciones/tramos/{id}/finalizar (Finalizar tramo - TRANSPORTISTA)"

test_endpoint \
    "Sin autenticación" \
    "POST" \
    "/api/operaciones/tramos/1/finalizar" \
    "" \
    "401"

if [ -n "$TRANSPORTISTA_TOKEN" ] && [ "$TRANSPORTISTA_TOKEN" != "null" ]; then
    test_endpoint \
        "Con rol TRANSPORTISTA (con permisos especiales)" \
        "POST" \
        "/api/operaciones/tramos/1/finalizar" \
        "$TRANSPORTISTA_TOKEN" \
        "200"
fi

##############################################################################
# RESUMEN DE RESULTADOS
##############################################################################

print_header "RESUMEN DE PRUEBAS"

echo -e "${BOLD}Total de pruebas:${NC} ${TESTS_TOTAL}"
echo -e "${GREEN}${BOLD}Pruebas exitosas:${NC} ${TESTS_PASSED}"
echo -e "${RED}${BOLD}Pruebas fallidas:${NC} ${TESTS_FAILED}"

if [ ${TESTS_FAILED} -gt 0 ]; then
    echo -e "\n${RED}${BOLD}Pruebas que fallaron:${NC}"
    for failed_test in "${FAILED_TESTS[@]}"; do
        echo -e "  ${RED}✗${NC} $failed_test"
    done
fi

# Cálculo de porcentaje
PASS_PERCENTAGE=$((TESTS_PASSED * 100 / TESTS_TOTAL))

echo -e "\n${BOLD}Tasa de éxito:${NC} ${PASS_PERCENTAGE}%"

if [ ${TESTS_FAILED} -eq 0 ]; then
    echo -e "\n${GREEN}${BOLD}¡TODAS LAS PRUEBAS PASARON! ✓${NC}\n"
    exit 0
else
    echo -e "\n${RED}${BOLD}Algunas pruebas fallaron. Revisa los detalles arriba.${NC}\n"
    exit 1
fi
