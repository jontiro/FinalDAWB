#!/bin/bash

# Script de Despliegue Automático para FinalDAWB en Tomcat10
# Autor: GitHub Copilot
# Fecha: 2025-11-23

set -e  # Salir si hay errores

echo "======================================"
echo "   DESPLIEGUE FINALDAWB - Tomcat10   "
echo "======================================"

# Variables
PROJECT_DIR="/home/jonathan/IdeaProjects/FinalDAWB"
WAR_FILE="$PROJECT_DIR/target/FinalDAWB-1.0-SNAPSHOT.war"
APP_NAME="FinalDAWB-1.0-SNAPSHOT"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir con color
print_green() { echo -e "${GREEN}✅ $1${NC}"; }
print_red() { echo -e "${RED}❌ $1${NC}"; }
print_yellow() { echo -e "${YELLOW}⚠️  $1${NC}"; }

# Paso 1: Verificar que el WAR existe
echo -e "\n[1/6] Verificando WAR compilado..."
if [ -f "$WAR_FILE" ]; then
    print_green "WAR encontrado: $WAR_FILE"
    ls -lh "$WAR_FILE"
else
    print_red "WAR no encontrado. Compilando proyecto..."
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests
    if [ -f "$WAR_FILE" ]; then
        print_green "WAR compilado exitosamente"
    else
        print_red "Error al compilar el proyecto"
        exit 1
    fi
fi

# Paso 2: Buscar el directorio webapps de Tomcat10
echo -e "\n[2/6] Buscando directorio webapps de Tomcat10..."

WEBAPPS_PATHS=(
    "/var/lib/tomcat10/webapps"
    "/usr/share/tomcat10/webapps"
    "/opt/tomcat10/webapps"
    "/opt/tomcat/webapps"
)

WEBAPPS_DIR=""
for path in "${WEBAPPS_PATHS[@]}"; do
    if [ -d "$path" ]; then
        WEBAPPS_DIR="$path"
        print_green "Directorio webapps encontrado: $WEBAPPS_DIR"
        break
    fi
done

if [ -z "$WEBAPPS_DIR" ]; then
    print_yellow "No se encontró webapps automáticamente"
    echo "Buscando en todo el sistema (esto puede tomar un momento)..."
    FOUND=$(sudo find /var /usr /opt -name "webapps" -type d 2>/dev/null | grep -i tomcat | head -1)
    if [ -n "$FOUND" ]; then
        WEBAPPS_DIR="$FOUND"
        print_green "Directorio webapps encontrado: $WEBAPPS_DIR"
    else
        print_red "No se pudo encontrar el directorio webapps de Tomcat"
        print_yellow "Por favor, ingresa la ruta manualmente:"
        read -p "Ruta a webapps: " WEBAPPS_DIR
        if [ ! -d "$WEBAPPS_DIR" ]; then
            print_red "El directorio no existe: $WEBAPPS_DIR"
            exit 1
        fi
    fi
fi

# Paso 3: Detener Tomcat
echo -e "\n[3/6] Deteniendo Tomcat..."

TOMCAT_SERVICES=("tomcat10" "tomcat")
TOMCAT_STOPPED=false

for service in "${TOMCAT_SERVICES[@]}"; do
    if systemctl is-active --quiet "$service" 2>/dev/null; then
        print_yellow "Deteniendo servicio: $service"
        sudo systemctl stop "$service"
        TOMCAT_STOPPED=true
        print_green "Servicio $service detenido"
        break
    fi
done

if [ "$TOMCAT_STOPPED" = false ]; then
    print_yellow "No se encontró un servicio de Tomcat corriendo"
fi

sleep 2

# Paso 4: Limpiar deployment anterior
echo -e "\n[4/6] Limpiando deployment anterior..."

if [ -d "$WEBAPPS_DIR/$APP_NAME" ] || [ -f "$WEBAPPS_DIR/$APP_NAME.war" ]; then
    print_yellow "Eliminando archivos anteriores..."
    sudo rm -rf "$WEBAPPS_DIR/$APP_NAME"*
    print_green "Archivos anteriores eliminados"
else
    print_green "No hay deployment anterior"
fi

# Paso 5: Copiar nuevo WAR
echo -e "\n[5/6] Copiando nuevo WAR..."

sudo cp "$WAR_FILE" "$WEBAPPS_DIR/"

if [ -f "$WEBAPPS_DIR/$(basename $WAR_FILE)" ]; then
    print_green "WAR copiado exitosamente a: $WEBAPPS_DIR/"

    # Cambiar permisos si es necesario
    sudo chown tomcat:tomcat "$WEBAPPS_DIR/$(basename $WAR_FILE)" 2>/dev/null || true
else
    print_red "Error al copiar el WAR"
    exit 1
fi

# Paso 6: Iniciar Tomcat
echo -e "\n[6/6] Iniciando Tomcat..."

TOMCAT_STARTED=false

for service in "${TOMCAT_SERVICES[@]}"; do
    if systemctl list-unit-files | grep -q "^$service.service"; then
        print_yellow "Iniciando servicio: $service"
        sudo systemctl start "$service"
        sleep 3
        if systemctl is-active --quiet "$service"; then
            TOMCAT_STARTED=true
            print_green "Servicio $service iniciado correctamente"
            break
        fi
    fi
done

if [ "$TOMCAT_STARTED" = false ]; then
    print_yellow "No se pudo iniciar Tomcat como servicio"
    print_yellow "Intenta iniciarlo manualmente desde IntelliJ IDEA"
fi

# Verificación final
echo -e "\n======================================"
echo "         DESPLIEGUE COMPLETADO        "
echo "======================================"

echo -e "\n📋 RESUMEN:"
echo "  WAR: $WAR_FILE"
echo "  Destino: $WEBAPPS_DIR/"
echo "  App: $APP_NAME"

echo -e "\n🧪 VERIFICACIÓN:"
echo "Espera 10-15 segundos y ejecuta:"
echo ""
echo "  curl http://localhost:8080/$APP_NAME/api/recetas"
echo ""
echo "Si responde [] → ✅ ¡Funciona!"
echo ""

# Verificación automática (opcional)
print_yellow "Esperando 15 segundos para que Tomcat despliegue la aplicación..."
sleep 15

echo -e "\nProbando endpoint..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/$APP_NAME/api/recetas 2>/dev/null || echo "000")

if [ "$HTTP_CODE" = "200" ]; then
    print_green "¡ÉXITO! La aplicación está funcionando correctamente"
    echo ""
    echo "Prueba el login:"
    echo "curl -X POST http://localhost:8080/$APP_NAME/api/auth/login \\"
    echo "  -H 'Content-Type: application/json' \\"
    echo "  -d '{\"usernameOrEmail\":\"testuser\",\"password\":\"password123\"}'"
elif [ "$HTTP_CODE" = "404" ]; then
    print_yellow "La aplicación aún se está desplegando o la URL es incorrecta"
    echo "Verifica: http://localhost:8080/$APP_NAME/"
elif [ "$HTTP_CODE" = "000" ]; then
    print_red "No se puede conectar a Tomcat en el puerto 8080"
    echo "Verifica que Tomcat esté corriendo: sudo systemctl status tomcat10"
else
    print_yellow "Respuesta HTTP: $HTTP_CODE"
    echo "Revisa los logs: sudo tail -50 /var/log/tomcat10/catalina.out"
fi

echo -e "\n📁 Archivos de ayuda disponibles:"
echo "  - DESPLIEGUE_TOMCAT10.md"
echo "  - SOLUCION_LIFECYCLE_ERROR.md"
echo "  - insomnia_collection.json"

echo -e "\n======================================"

