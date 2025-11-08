#!/bin/bash

##############################################################################
# Script de Inicio Completo del Sistema TPI-Backend
# 
# Inicia todos los servicios necesarios en el orden correcto:
# 1. Keycloak (con PostgreSQL)
# 2. API Gateway
# 3. Servicio Flota
# 4. Servicio Operaciones
#
# Requisitos: Docker, Docker Compose, Maven
##############################################################################

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'
BOLD='\033[1m'

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo -e "${BOLD}${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${BLUE}  INICIO COMPLETO DEL SISTEMA TPI-BACKEND${NC}"
echo -e "${BOLD}${BLUE}═══════════════════════════════════════════════════════════════${NC}\n"

##############################################################################
# VERIFICAR PRERREQUISITOS
##############################################################################

echo -e "${BOLD}${CYAN}▶ Verificando prerrequisitos...${NC}"

# Verificar Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker instalado${NC}"

# Verificar Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}✗ Docker Compose no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker Compose instalado${NC}"

# Verificar Maven
if ! command -v mvn &> /dev/null && [ ! -f "$PROJECT_ROOT/servicio-flota/mvnw" ]; then
    echo -e "${RED}✗ Maven no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven disponible${NC}"

##############################################################################
# PASO 1: INICIAR KEYCLOAK
##############################################################################

echo -e "\n${BOLD}${CYAN}▶ Paso 1: Iniciando Keycloak...${NC}"

cd "$PROJECT_ROOT/docker"

# Verificar si Keycloak ya está corriendo
if curl -s http://localhost:8180/health/ready > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠ Keycloak ya está corriendo${NC}"
else
    echo -e "${BLUE}Iniciando contenedores Docker...${NC}"
    docker-compose -f docker-compose-keycloak.yml up -d
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}✗ Error al iniciar Keycloak${NC}"
        exit 1
    fi
    
    # Esperar a que Keycloak esté listo
    echo -e "${BLUE}Esperando a que Keycloak esté listo...${NC}"
    MAX_ATTEMPTS=30
    ATTEMPT=0
    
    while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
        if curl -s http://localhost:8180/health/ready > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Keycloak está listo${NC}"
            break
        fi
        
        ATTEMPT=$((ATTEMPT + 1))
        echo -n "."
        sleep 2
    done
    
    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        echo -e "\n${RED}✗ Timeout esperando a Keycloak${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✓ Keycloak: http://localhost:8180${NC}"

##############################################################################
# PASO 2: COMPILAR PROYECTOS
##############################################################################

echo -e "\n${BOLD}${CYAN}▶ Paso 2: Compilando proyectos...${NC}"

cd "$PROJECT_ROOT"

# Compilar servicio-flota
echo -e "${BLUE}Compilando servicio-flota...${NC}"
cd "$PROJECT_ROOT/servicio-flota"
if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests > /dev/null 2>&1
else
    mvn clean package -DskipTests > /dev/null 2>&1
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ servicio-flota compilado${NC}"
else
    echo -e "${RED}✗ Error compilando servicio-flota${NC}"
fi

# Compilar servicio-operaciones
echo -e "${BLUE}Compilando servicio-operaciones...${NC}"
cd "$PROJECT_ROOT/servicio-operaciones"
if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests > /dev/null 2>&1
else
    mvn clean package -DskipTests > /dev/null 2>&1
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ servicio-operaciones compilado${NC}"
else
    echo -e "${RED}✗ Error compilando servicio-operaciones${NC}"
fi

# Compilar api-gateway
echo -e "${BLUE}Compilando api-gateway...${NC}"
cd "$PROJECT_ROOT/api-gateway"
if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests > /dev/null 2>&1
else
    mvn clean package -DskipTests > /dev/null 2>&1
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ api-gateway compilado${NC}"
else
    echo -e "${RED}✗ Error compilando api-gateway${NC}"
fi

##############################################################################
# MENSAJE PARA INICIAR SERVICIOS
##############################################################################

echo -e "\n${BOLD}${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${GREEN}  KEYCLOAK INICIADO Y PROYECTOS COMPILADOS${NC}"
echo -e "${BOLD}${BLUE}═══════════════════════════════════════════════════════════════${NC}\n"

echo -e "${BOLD}${CYAN}Para iniciar los servicios Spring Boot, abre 3 terminales:${NC}\n"

echo -e "${YELLOW}Terminal 1 - Servicio Flota:${NC}"
echo -e "  cd $PROJECT_ROOT/servicio-flota"
echo -e "  ./mvnw spring-boot:run\n"

echo -e "${YELLOW}Terminal 2 - Servicio Operaciones:${NC}"
echo -e "  cd $PROJECT_ROOT/servicio-operaciones"
echo -e "  ./mvnw spring-boot:run\n"

echo -e "${YELLOW}Terminal 3 - API Gateway:${NC}"
echo -e "  cd $PROJECT_ROOT/api-gateway"
echo -e "  ./mvnw spring-boot:run\n"

echo -e "${BOLD}${CYAN}Después de iniciar todos los servicios, ejecuta las pruebas:${NC}"
echo -e "  ./scripts/test-endpoints-security.sh\n"

echo -e "${BOLD}URLs de los servicios:${NC}"
echo -e "  Keycloak:     ${BLUE}http://localhost:8180${NC} (admin/admin123)"
echo -e "  API Gateway:  ${BLUE}http://localhost:8080${NC}"
echo -e "  Flota:        ${BLUE}http://localhost:8081${NC}"
echo -e "  Operaciones:  ${BLUE}http://localhost:8082${NC}"

echo -e "\n${BOLD}${GREEN}¡Sistema preparado! Inicia los servicios Spring Boot manualmente.${NC}\n"
