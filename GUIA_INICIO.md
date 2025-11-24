# 🎯 GUÍA RÁPIDA - Iniciar Tu Aplicación

## ✅ BUENAS NOTICIAS

**Ya tienes los datos en la base de datos:**
- ✅ 3 Roles (ADMIN, USER, VISITOR)
- ✅ 1 Usuario de prueba (testuser / password123)
- ✅ WAR compilado correctamente

**Solo necesitas iniciar Tomcat con tu aplicación.**

---

## 🚀 PASO A PASO - Iniciar desde IntelliJ IDEA

### 1. Abrir IntelliJ IDEA
- Abre el proyecto: `/home/jonathan/IdeaProjects/FinalDAWB`

### 2. Configurar Tomcat (Si no lo has hecho)

#### A. Ir a Run Configurations:
- Menu: `Run` → `Edit Configurations...`

#### B. Agregar Tomcat Server:
- Click en `+` (Add New Configuration)
- Selecciona: `Tomcat Server` → `Local`

#### C. Configurar Server Tab:
- **Name:** `Tomcat FinalDAWB`
- **Application server:** Click en `Configure...`
  - **Tomcat Home:** `/opt/tomcat` (o donde esté instalado)
  - Click `OK`
- **HTTP port:** `8080`
- **JMX port:** `1099`

#### D. Configurar Deployment Tab:
- Click en `+` → `Artifact...`
- Selecciona: `FinalDAWB:war exploded` o `FinalDAWB:war`
- **Application context:** `/FinalDAWB-1.0-SNAPSHOT`
- Click `OK`

### 3. Iniciar la Aplicación

#### Opción 1: Run (Normal)
- Click en el botón verde ▶️ en la toolbar
- O presiona: `Shift + F10`
- O menu: `Run` → `Run 'Tomcat FinalDAWB'`

#### Opción 2: Debug (Para desarrollo)
- Click en el botón debug 🐛 en la toolbar
- O presiona: `Shift + F9`
- O menu: `Run` → `Debug 'Tomcat FinalDAWB'`

### 4. Verificar que Inició

En la consola de IntelliJ deberías ver:
```
[INFO] Artifact FinalDAWB:war exploded: Artifact is deployed successfully
Server startup in [xxx] milliseconds
```

---

## 🧪 PROBAR LA APLICACIÓN

### 1. Verificar en el Navegador
Abre: http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/

**Deberías ver:** Tu página index.html

### 2. Probar Login en Insomnia

**Endpoint:** `POST http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/auth/login`

**Body:**
```json
{
  "usernameOrEmail": "testuser",
  "password": "password123"
}
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "ADMIN"
}
```

### 3. Registrar Nuevo Usuario

**Endpoint:** `POST http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/auth/register`

**Body:**
```json
{
  "username": "usuario1",
  "email": "usuario1@example.com",
  "password": "password123"
}
```

### 4. Crear Una Receta

**Endpoint:** `POST http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/recetas`

**Body:**
```json
{
  "titulo": "Tacos al Pastor",
  "descripcion": "Deliciosos tacos mexicanos",
  "tiempoPreparacion": 45,
  "creadorId": 1,
  "privacidad": false,
  "pasos": [
    {
      "orden": 1,
      "descripcion": "Marinar la carne con achiote"
    },
    {
      "orden": 2,
      "descripcion": "Cocinar en el trompo"
    },
    {
      "orden": 3,
      "descripcion": "Servir en tortillas con piña"
    }
  ],
  "tags": ["Mexicano", "Rápido"]
}
```

### 5. Ver Todas las Recetas

**Endpoint:** `GET http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/recetas`

---

## 🔧 ALTERNATIVA: Iniciar Tomcat Manualmente

Si prefieres no usar IntelliJ IDEA:

### 1. Copiar el WAR
```bash
cd /home/jonathan/IdeaProjects/FinalDAWB
sudo cp target/FinalDAWB-1.0-SNAPSHOT.war /opt/tomcat/webapps/
```

### 2. Iniciar Tomcat
```bash
# Si está como servicio:
sudo systemctl start tomcat

# O manualmente:
/opt/tomcat/bin/startup.sh
```

### 3. Ver los Logs
```bash
tail -f /opt/tomcat/logs/catalina.out
```

**Espera a ver:**
```
Server startup in [xxx] milliseconds
```

### 4. Verificar que Desplegó
```bash
ls -la /opt/tomcat/webapps/FinalDAWB-1.0-SNAPSHOT/
```
**Debe existir** el directorio con archivos WEB-INF, etc.

---

## ⚡ ATAJOS DE TECLADO (IntelliJ)

| Acción | Windows/Linux | macOS |
|--------|---------------|-------|
| Run | `Shift + F10` | `⌃R` |
| Debug | `Shift + F9` | `⌃D` |
| Stop | `Ctrl + F2` | `⌘F2` |
| Restart | `Ctrl + F5` | `⌘F5` |

---

## 📋 COLECCIÓN DE INSOMNIA

Usa el archivo que ya tienes:
```
/home/jonathan/IdeaProjects/FinalDAWB/insomnia_collection.json
```

**Para importar:**
1. Abre Insomnia
2. `Application` → `Preferences` → `Data` → `Import Data`
3. Selecciona: `From File`
4. Elige: `insomnia_collection.json`

**Ya incluye:**
- ✅ Login
- ✅ Registro
- ✅ Crear recetas (3 ejemplos)
- ✅ Ver recetas
- ✅ Ver receta por ID
- ✅ Eliminar receta

---

## ❌ PROBLEMAS COMUNES

### Error: "Address already in use: bind"
**Causa:** El puerto 8080 está ocupado
**Solución:**
```bash
# Ver qué está usando el puerto 8080
sudo lsof -i :8080

# Matar el proceso si es necesario
kill -9 [PID]
```

### Error: "Cannot find Tomcat"
**Solución:**
1. Verifica que Tomcat esté en `/opt/tomcat`
2. En IntelliJ: `Run` → `Edit Configurations...`
3. Verifica el path en `Application server`

### Error: "Artifact not deployed"
**Solución:**
1. `Build` → `Rebuild Project`
2. `Build` → `Build Artifacts...` → `FinalDAWB:war` → `Build`
3. Intenta iniciar Tomcat de nuevo

### Error: La página no carga
**Verificar:**
```bash
# ¿Tomcat está corriendo?
ps aux | grep tomcat

# ¿La app está desplegada?
ls /opt/tomcat/webapps/ | grep FinalDAWB

# ¿Hay errores en los logs?
tail -100 /opt/tomcat/logs/catalina.out | grep -i error
```

---

## 🎯 CHECKLIST FINAL

Antes de probar con Insomnia:

- [ ] Tomcat está corriendo
- [ ] Aplicación desplegada en `/opt/tomcat/webapps/`
- [ ] Base de datos MySQL corriendo
- [ ] Roles creados en la BD
- [ ] Usuario testuser existe
- [ ] URL correcta: `http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/`

---

## 🆘 SI NADA FUNCIONA

### Script de Diagnóstico Completo

Ejecuta este script para ver el estado del sistema:

```bash
#!/bin/bash
echo "=== DIAGNÓSTICO FINALDAWB ==="

echo -e "\n1. ¿MySQL está corriendo?"
systemctl is-active mariadb || systemctl is-active mysql

echo -e "\n2. ¿Hay roles en la BD?"
mysql -u root -p020320 -e "USE cocina; SELECT COUNT(*) FROM roles;" 2>/dev/null

echo -e "\n3. ¿Hay usuarios en la BD?"
mysql -u root -p020320 -e "USE cocina; SELECT username FROM usuarios;" 2>/dev/null

echo -e "\n4. ¿El WAR existe?"
ls -lh /home/jonathan/IdeaProjects/FinalDAWB/target/FinalDAWB-1.0-SNAPSHOT.war

echo -e "\n5. ¿Tomcat está corriendo?"
ps aux | grep tomcat | grep -v grep | wc -l

echo -e "\n6. ¿La app está desplegada?"
ls /opt/tomcat/webapps/ | grep FinalDAWB

echo -e "\n7. ¿El puerto 8080 está escuchando?"
sudo lsof -i :8080 | grep LISTEN

echo -e "\n8. ¿La API responde?"
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/recetas

echo -e "\n=== FIN DIAGNÓSTICO ===\n"
```

Guarda como `diagnostico.sh` y ejecuta:
```bash
chmod +x diagnostico.sh
./diagnostico.sh
```

---

## 📞 RESUMEN

1. **Inicia Tomcat desde IntelliJ:** Click en ▶️ (Run)
2. **Espera a que despliegue:** Ver logs en la consola
3. **Importa la colección en Insomnia:** Usa `insomnia_collection.json`
4. **Prueba el Login:** Usuario `testuser` / Password `password123`
5. **Crea recetas:** Usa `creadorId: 1`

**¡Ya tienes todo listo! Solo falta iniciar Tomcat.** 🚀

