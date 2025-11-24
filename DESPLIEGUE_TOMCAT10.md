# 🚀 DESPLIEGUE EN TOMCAT10 (/var/lib/tomcat10/)

## 📍 UBICACIÓN DE TU TOMCAT

Según indicaste: `/var/lib/tomcat10/`

---

## ✅ OPCIÓN 1: DESPLIEGUE DESDE INTELLIJ IDEA (RECOMENDADO)

IntelliJ gestiona Tomcat automáticamente. **No necesitas copiar archivos manualmente.**

### Paso 1: Configurar Tomcat en IntelliJ

1. **Abre IntelliJ IDEA** con tu proyecto FinalDAWB

2. **Ir a Run Configurations:**
   - Menu: `Run` → `Edit Configurations...`

3. **Agregar/Editar Tomcat Server:**
   - Si no existe: Click en `+` → `Tomcat Server` → `Local`
   - Si existe: Selecciona la configuración existente

4. **Configurar el Server Tab:**
   ```
   Name: Tomcat 10
   Application server: Configure...
   ```

5. **En la ventana de configuración del servidor:**
   ```
   Tomcat Home: /var/lib/tomcat10
   ```
   (O busca la carpeta donde esté instalado Tomcat)

6. **HTTP port:** `8080` (o el puerto que uses)

7. **Pestaña Deployment:**
   - Click en `+` → `Artifact...`
   - Selecciona: `FinalDAWB:war exploded` o `FinalDAWB:war`
   - **Application context:** `/FinalDAWB-1.0-SNAPSHOT`

8. **Click OK** para guardar

### Paso 2: Iniciar la Aplicación

```
Método 1: Click en ▶️ (Run) en la toolbar
Método 2: Presiona Shift + F10
Método 3: Menu Run → Run 'Tomcat 10'
```

### Paso 3: Verificar

En la consola de IntelliJ verás:
```
[INFO] Artifact FinalDAWB:war exploded: Artifact is deployed successfully
Server startup in [xxx] milliseconds
```

---

## ✅ OPCIÓN 2: DESPLIEGUE MANUAL

Si Tomcat está instalado en `/var/lib/tomcat10/`:

### Paso 1: Ubicar el Directorio webapps

```bash
# Verificar si existe
ls -la /var/lib/tomcat10/webapps/

# Si no existe, buscar la ubicación real
sudo find /var/lib/tomcat10 -name "webapps" -type d
```

### Paso 2: Copiar el WAR

```bash
# El WAR ya está compilado
cd /home/jonathan/IdeaProjects/FinalDAWB

# Copiar al webapps de Tomcat (ajusta la ruta si es diferente)
sudo cp target/FinalDAWB-1.0-SNAPSHOT.war /var/lib/tomcat10/webapps/

# O si webapps está en otro lugar:
# sudo cp target/FinalDAWB-1.0-SNAPSHOT.war /var/lib/tomcat10/[RUTA_CORRECTA]/webapps/
```

### Paso 3: Iniciar Tomcat

```bash
# Opción A: Como servicio
sudo systemctl start tomcat10

# Opción B: Como servicio con nombre diferente
sudo systemctl start tomcat

# Opción C: Manualmente (si no es servicio)
sudo /var/lib/tomcat10/bin/startup.sh
# O
sudo /usr/share/tomcat10/bin/startup.sh
```

### Paso 4: Verificar Logs

```bash
# Logs comunes de Tomcat10
sudo tail -f /var/log/tomcat10/catalina.out

# O en la ubicación del servidor
sudo tail -f /var/lib/tomcat10/logs/catalina.out
```

---

## ✅ OPCIÓN 3: DETERMINAR LA UBICACIÓN EXACTA DE TOMCAT

Si no estás seguro de la ubicación exacta, ejecuta:

### Script de Detección

```bash
#!/bin/bash
echo "=== BUSCANDO TOMCAT ==="

echo -e "\n1. Buscando catalina.sh:"
sudo find / -name "catalina.sh" 2>/dev/null | head -5

echo -e "\n2. Buscando directorio webapps:"
sudo find / -name "webapps" -type d 2>/dev/null | grep tomcat | head -5

echo -e "\n3. Servicios de Tomcat:"
systemctl list-units --type=service | grep tomcat

echo -e "\n4. Procesos de Tomcat:"
ps aux | grep tomcat | grep -v grep

echo -e "\n5. Paquetes de Tomcat instalados:"
dpkg -l | grep tomcat

echo -e "\n6. Variables de entorno:"
env | grep -i tomcat

echo -e "\n=== FIN DE BÚSQUEDA ==="
```

Guarda como `find-tomcat.sh` y ejecuta:
```bash
chmod +x find-tomcat.sh
./find-tomcat.sh
```

---

## 🎯 CONFIGURACIONES COMUNES DE TOMCAT10

Dependiendo de cómo instalaste Tomcat, puede estar en:

### Instalación por Paquete (apt)
```
CATALINA_HOME: /usr/share/tomcat10
CATALINA_BASE: /var/lib/tomcat10
webapps: /var/lib/tomcat10/webapps
logs: /var/log/tomcat10
config: /etc/tomcat10
```

### Instalación Manual
```
CATALINA_HOME: /opt/tomcat10
webapps: /opt/tomcat10/webapps
logs: /opt/tomcat10/logs
config: /opt/tomcat10/conf
```

---

## 🧪 VERIFICAR DESPLIEGUE

Una vez desplegado, prueba:

### 1. Página Principal
```bash
curl http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/
```

### 2. API - Recetas
```bash
curl http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/recetas
```
**Esperado:** `[]`

### 3. Login
```bash
curl -X POST http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"password123"}'
```

---

## ⚠️ PROBLEMAS COMUNES

### Error: Permission denied al copiar WAR

```bash
# Solución: Usa sudo
sudo cp target/FinalDAWB-1.0-SNAPSHOT.war /var/lib/tomcat10/webapps/

# O cambia permisos temporalmente
sudo chown $USER:$USER /var/lib/tomcat10/webapps/
cp target/FinalDAWB-1.0-SNAPSHOT.war /var/lib/tomcat10/webapps/
sudo chown tomcat:tomcat /var/lib/tomcat10/webapps/FinalDAWB-1.0-SNAPSHOT.war
```

### Error: Tomcat no inicia

```bash
# Ver el status
sudo systemctl status tomcat10

# Ver los logs de error
sudo journalctl -u tomcat10 -n 50

# Ver logs de Tomcat
sudo tail -100 /var/log/tomcat10/catalina.out | grep -i error
```

### Error: Puerto 8080 ocupado

```bash
# Ver qué lo usa
sudo lsof -i :8080

# Cambiar puerto en IntelliJ o en server.xml
# Archivo: /etc/tomcat10/server.xml o /var/lib/tomcat10/conf/server.xml
# Buscar: <Connector port="8080"
# Cambiar a: <Connector port="8081"
```

---

## 📋 CHECKLIST DE DESPLIEGUE

### Pre-requisitos:
- [ ] Tomcat10 instalado
- [ ] MySQL/MariaDB corriendo
- [ ] Base de datos `cocina` existe con datos
- [ ] WAR compilado: `target/FinalDAWB-1.0-SNAPSHOT.war`

### Despliegue:
- [ ] WAR copiado a webapps (manual) O configurado en IntelliJ
- [ ] Tomcat iniciado
- [ ] Sin errores en logs

### Verificación:
- [ ] Puerto 8080 responde
- [ ] Página principal carga
- [ ] API `/api/recetas` responde
- [ ] Login funciona

---

## 🎓 RECOMENDACIÓN

**USA INTELLIJ IDEA** para gestionar Tomcat:

✅ **Ventajas:**
- Deployment automático
- Hot reload de cambios
- Debugging integrado
- Logs en la consola
- No necesitas permisos sudo
- No necesitas copiar archivos manualmente

❌ **Despliegue Manual:**
- Requiere sudo
- Copiar WAR cada vez que cambies código
- Sin debugging directo
- Logs en archivos externos

---

## 🚀 QUICK START (Lo Más Rápido)

```bash
# 1. Verifica que el WAR existe
ls -lh /home/jonathan/IdeaProjects/FinalDAWB/target/FinalDAWB-1.0-SNAPSHOT.war

# 2. Encuentra tu webapps
sudo find /var -name "webapps" -type d 2>/dev/null | grep tomcat

# 3. Copia el WAR (ajusta la ruta según lo que encontraste)
sudo cp /home/jonathan/IdeaProjects/FinalDAWB/target/FinalDAWB-1.0-SNAPSHOT.war /var/lib/tomcat10/webapps/

# 4. Inicia Tomcat
sudo systemctl start tomcat10

# 5. Espera 10 segundos y verifica
sleep 10
curl http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/recetas
```

---

## 📞 RESUMEN

### Si usas IntelliJ:
1. Configura Tomcat en Run Configurations
2. Click en ▶️ Run
3. ¡Listo!

### Si despliegas manualmente:
1. Encuentra webapps: `sudo find /var -name webapps | grep tomcat`
2. Copia WAR: `sudo cp target/*.war [WEBAPPS_PATH]/`
3. Inicia Tomcat: `sudo systemctl start tomcat10`
4. Verifica: `curl localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/recetas`

**El WAR ya está compilado y listo. Solo necesitas desplegarlo.** ✅

