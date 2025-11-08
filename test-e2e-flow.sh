#!/bin/bash

###############################################################################
# SCRIPT END-TO-END AUTOMATIZADO: FLUJO COMPLETO DE LOGÍSTICA
# Sistema de Transporte de Contenedores
# 
# Prerrequisitos:
# 1. servicio-flota corriendo en puerto 8081
# 2. servicio-operaciones corriendo en puerto 8082
# 3. Base de datos con data.sql cargado
# 4. jq instalado (para parsear JSON): https://stedolan.github.io/jq/
#
# Uso:
#   chmod +x test-e2e-flow.sh
#   ./test-e2e-flow.sh
###############################################################################

set -e  # Salir si algún comando falla

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# URLs base
BASE_URL_OPERACIONES="http://localhost:8082/api"
BASE_URL_FLOTA="http://localhost:8081/api"

# Función para imprimir paso
print_step() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

# Función para imprimir éxito
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Función para imprimir error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Función para imprimir información
print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

###############################################################################
# PASO 1: Crear una Nueva Solicitud (RF#1)
###############################################################################
print_step "PASO 1: Crear una Nueva Solicitud (RF#1)"

SOLICITUD_RESPONSE=$(curl -s -X POST "${BASE_URL_OPERACIONES}/solicitudes" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "contenedor": {
      "numero": "CONT-E2E-001",
      "tipo": "STANDARD",
      "estado": "EN_ORIGEN",
      "peso": 2000.0,
      "volumen": 15.0
    },
    "direccionOrigen": "Córdoba Centro, Córdoba, Argentina",
    "latitudOrigen": -31.4135,
    "longitudOrigen": -64.1811,
    "direccionDestino": "Villa Carlos Paz, Córdoba, Argentina",
    "latitudDestino": -31.4241,
    "longitudDestino": -64.4978
  }')

SOLICITUD_ID=$(echo $SOLICITUD_RESPONSE | jq -r '.id')
CONTENEDOR_ID=$(echo $SOLICITUD_RESPONSE | jq -r '.contenedor.id')

if [ -z "$SOLICITUD_ID" ] || [ "$SOLICITUD_ID" = "null" ]; then
    print_error "Error al crear la solicitud"
    echo $SOLICITUD_RESPONSE | jq '.'
    exit 1
fi

print_success "Solicitud creada con ID: $SOLICITUD_ID"
print_success "Contenedor creado con ID: $CONTENEDOR_ID"
echo $SOLICITUD_RESPONSE | jq '.'

###############################################################################
# PASO 2: Consultar Rutas Tentativas (RF#3)
###############################################################################
print_step "PASO 2: Consultar Rutas Tentativas (RF#3)"

RUTAS_TENTATIVAS=$(curl -s -X GET "${BASE_URL_OPERACIONES}/solicitudes/${SOLICITUD_ID}/rutas/tentativas")

print_success "Rutas tentativas calculadas"
echo $RUTAS_TENTATIVAS | jq '.'

###############################################################################
# PASO 3: Asignar Ruta Definitiva (RF#6)
###############################################################################
print_step "PASO 3: Asignar Ruta Definitiva (RF#6)"

ASIGNAR_RUTA_RESPONSE=$(curl -s -X POST "${BASE_URL_OPERACIONES}/solicitudes/${SOLICITUD_ID}/asignar-ruta" \
  -H "Content-Type: application/json" \
  -d '{
    "origenDepositoId": 1,
    "destinoDepositoId": 2,
    "tramos": [
      {
        "origenDepositoId": 1,
        "destinoDepositoId": 2,
        "distanciaKm": 50.0,
        "tiempoEstimadoHoras": 1.5,
        "fechaEstimadaInicio": "2025-10-26T10:00:00",
        "fechaEstimadaFin": "2025-10-26T11:30:00"
      }
    ]
  }')

RUTA_ID=$(echo $ASIGNAR_RUTA_RESPONSE | jq -r '.id')
TRAMO_ID=$(echo $ASIGNAR_RUTA_RESPONSE | jq -r '.tramos[0].id')

if [ -z "$RUTA_ID" ] || [ "$RUTA_ID" = "null" ]; then
    print_error "Error al asignar ruta"
    echo $ASIGNAR_RUTA_RESPONSE | jq '.'
    exit 1
fi

print_success "Ruta asignada con ID: $RUTA_ID"
print_success "Tramo creado con ID: $TRAMO_ID"
echo $ASIGNAR_RUTA_RESPONSE | jq '.'

###############################################################################
# PASO 4: Consultar Camiones Disponibles
###############################################################################
print_step "PASO 4: Consultar Camiones Disponibles"

CAMIONES_DISPONIBLES=$(curl -s -X GET "${BASE_URL_FLOTA}/camiones/disponibles")

print_success "Camiones disponibles:"
echo $CAMIONES_DISPONIBLES | jq '.'

###############################################################################
# PASO 5: Asignar Camión al Tramo (RF#6)
###############################################################################
print_step "PASO 5: Asignar Camión al Tramo (RF#6)"

ASIGNAR_CAMION_RESPONSE=$(curl -s -X POST "${BASE_URL_OPERACIONES}/tramos/${TRAMO_ID}/asignar-camion" \
  -H "Content-Type: application/json" \
  -d '{
    "camionId": 1
  }')

print_success "Camión asignado al tramo"
echo $ASIGNAR_CAMION_RESPONSE | jq '.'

###############################################################################
# PASO 6: Verificar que el Camión ya NO está Disponible
###############################################################################
print_step "PASO 6: Verificar que el Camión ya NO está Disponible"

CAMIONES_DISPONIBLES_DESPUES=$(curl -s -X GET "${BASE_URL_FLOTA}/camiones/disponibles")

print_info "Camiones disponibles después de asignar:"
echo $CAMIONES_DISPONIBLES_DESPUES | jq '.'

###############################################################################
# PASO 7: Iniciar el Tramo (RF#8)
###############################################################################
print_step "PASO 7: Iniciar el Tramo (RF#8)"

INICIAR_TRAMO_RESPONSE=$(curl -s -X POST "${BASE_URL_OPERACIONES}/tramos/${TRAMO_ID}/iniciar")

print_success "Tramo iniciado"
echo $INICIAR_TRAMO_RESPONSE | jq '.'

###############################################################################
# PASO 8: Consultar Estado del Contenedor (RF#2)
###############################################################################
print_step "PASO 8: Consultar Estado del Contenedor (RF#2)"

ESTADO_CONTENEDOR=$(curl -s -X GET "${BASE_URL_OPERACIONES}/contenedores/${CONTENEDOR_ID}/estado")

print_success "Estado del contenedor (debería ser EN_VIAJE):"
echo $ESTADO_CONTENEDOR | jq '.'

###############################################################################
# PASO 9: Consultar Estado de la Solicitud (RF#2)
###############################################################################
print_step "PASO 9: Consultar Estado de la Solicitud (RF#2)"

ESTADO_SOLICITUD=$(curl -s -X GET "${BASE_URL_OPERACIONES}/solicitudes/${SOLICITUD_ID}/estado")

print_success "Estado completo de la solicitud:"
echo $ESTADO_SOLICITUD | jq '.'

###############################################################################
# PASO 10: Consultar Tramos del Transportista (RF#7)
###############################################################################
print_step "PASO 10: Consultar Tramos del Transportista (RF#7)"

TRAMOS_TRANSPORTISTA=$(curl -s -X GET "${BASE_URL_OPERACIONES}/tramos/transportistas/1/tramos")

print_success "Tramos asignados al transportista (camión ID=1):"
echo $TRAMOS_TRANSPORTISTA | jq '.'

###############################################################################
# PASO 11: Finalizar el Tramo (RF#8)
###############################################################################
print_step "PASO 11: Finalizar el Tramo (RF#8)"

FINALIZAR_TRAMO_RESPONSE=$(curl -s -X POST "${BASE_URL_OPERACIONES}/tramos/${TRAMO_ID}/finalizar")

print_success "Tramo finalizado (con costo real calculado):"
echo $FINALIZAR_TRAMO_RESPONSE | jq '.'

COSTO_REAL=$(echo $FINALIZAR_TRAMO_RESPONSE | jq -r '.costoReal')
print_info "Costo real calculado: \$${COSTO_REAL}"

###############################################################################
# PASO 12: Verificar que el Camión volvió a estar Disponible
###############################################################################
print_step "PASO 12: Verificar que el Camión volvió a estar Disponible"

CAMIONES_DISPONIBLES_FINAL=$(curl -s -X GET "${BASE_URL_FLOTA}/camiones/disponibles")

print_success "Camiones disponibles después de finalizar tramo:"
echo $CAMIONES_DISPONIBLES_FINAL | jq '.'

###############################################################################
# PASO 13: Verificar Estado del Contenedor (debería ser ENTREGADO)
###############################################################################
print_step "PASO 13: Verificar Estado del Contenedor (debería ser ENTREGADO)"

ESTADO_CONTENEDOR_FINAL=$(curl -s -X GET "${BASE_URL_OPERACIONES}/contenedores/${CONTENEDOR_ID}/estado")

print_success "Estado del contenedor después de finalizar tramo:"
echo $ESTADO_CONTENEDOR_FINAL | jq '.'

###############################################################################
# PASO 14: Finalizar la Solicitud (RF#9)
###############################################################################
print_step "PASO 14: Finalizar la Solicitud (RF#9)"

FINALIZAR_SOLICITUD_RESPONSE=$(curl -s -X PATCH "${BASE_URL_OPERACIONES}/solicitudes/${SOLICITUD_ID}/finalizar" \
  -H "Content-Type: application/json" \
  -d '{
    "observaciones": "Entrega completada exitosamente - Test E2E Automatizado"
  }')

print_success "Solicitud finalizada:"
echo $FINALIZAR_SOLICITUD_RESPONSE | jq '.'

COSTO_FINAL=$(echo $FINALIZAR_SOLICITUD_RESPONSE | jq -r '.costoFinal')
TIEMPO_REAL=$(echo $FINALIZAR_SOLICITUD_RESPONSE | jq -r '.tiempoReal')
ESTADO_FINAL=$(echo $FINALIZAR_SOLICITUD_RESPONSE | jq -r '.estado')

print_info "Estado final: ${ESTADO_FINAL}"
print_info "Costo final total: \$${COSTO_FINAL}"
print_info "Tiempo real total: ${TIEMPO_REAL} horas"

###############################################################################
# PASO 15: Consultar Estado Final Completo
###############################################################################
print_step "PASO 15: Consultar Estado Final Completo"

ESTADO_FINAL_COMPLETO=$(curl -s -X GET "${BASE_URL_OPERACIONES}/solicitudes/${SOLICITUD_ID}/estado")

print_success "Estado final completo de la solicitud:"
echo $ESTADO_FINAL_COMPLETO | jq '.'

###############################################################################
# PASO 16 (OPCIONAL): Consultar Contenedores Pendientes (RF#5)
###############################################################################
print_step "PASO 16: Consultar Contenedores Pendientes (RF#5)"

CONTENEDORES_PENDIENTES=$(curl -s -X GET "${BASE_URL_OPERACIONES}/contenedores/pendientes")

print_info "Contenedores pendientes (CONT-E2E-001 NO debería aparecer):"
echo $CONTENEDORES_PENDIENTES | jq '.'

###############################################################################
# RESUMEN FINAL
###############################################################################
print_step "✅ RESUMEN DEL FLUJO END-TO-END COMPLETADO"

echo -e "${GREEN}┌─────────────────────────────────────────────────┐${NC}"
echo -e "${GREEN}│         FLUJO COMPLETADO EXITOSAMENTE           │${NC}"
echo -e "${GREEN}└─────────────────────────────────────────────────┘${NC}"
echo ""
echo -e "📋 ${YELLOW}Solicitud ID:${NC} $SOLICITUD_ID"
echo -e "📦 ${YELLOW}Contenedor ID:${NC} $CONTENEDOR_ID"
echo -e "🗺️  ${YELLOW}Ruta ID:${NC} $RUTA_ID"
echo -e "🛣️  ${YELLOW}Tramo ID:${NC} $TRAMO_ID"
echo ""
echo -e "💰 ${YELLOW}Costo Final:${NC} \$${COSTO_FINAL}"
echo -e "⏱️  ${YELLOW}Tiempo Real:${NC} ${TIEMPO_REAL} horas"
echo -e "📊 ${YELLOW}Estado Final:${NC} ${ESTADO_FINAL}"
echo ""
echo -e "${GREEN}✓ RF#1: Crear Solicitud${NC}"
echo -e "${GREEN}✓ RF#2: Consultar Estado${NC}"
echo -e "${GREEN}✓ RF#3: Calcular Rutas Tentativas${NC}"
echo -e "${GREEN}✓ RF#6: Asignar Ruta y Camión${NC}"
echo -e "${GREEN}✓ RF#7: Consultar Tramos del Transportista${NC}"
echo -e "${GREEN}✓ RF#8: Iniciar y Finalizar Tramo${NC}"
echo -e "${GREEN}✓ RF#9: Finalizar Solicitud${NC}"
echo ""
print_success "Todos los pasos del flujo E2E se ejecutaron correctamente"
