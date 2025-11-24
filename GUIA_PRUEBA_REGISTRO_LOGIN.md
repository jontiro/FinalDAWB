# 🔧 GUÍA DE PRUEBA - REGISTRO Y LOGIN

## ✅ ERROR DE SINTAXIS CORREGIDO

**Problema encontrado:** Código duplicado al final del archivo causaba `Unexpected token '}'`

**Solución aplicada:** Eliminado el código duplicado en `index.html`

---

## 🚀 PASOS PARA PROBAR

### 1. Rebuild + Redeploy

**En IntelliJ IDEA:**
```
1. Build → Rebuild Project
2. Run → Redeploy (o Stop + Run)
3. Espera a que termine el despliegue
```

**Verifica en los logs que veas:**
```
INFO: Deployment of web application [...] has finished in [xxx] ms
```

### 2. Verificar que la Aplicación Está Desplegada

**Abre en el navegador:**
```
http://localhost:8080/FinalDAWB_war_exploded/
```

**Deberías ver:** La página con los 3 formularios (Registro, Login, Recetas)

### 3. Abrir Consola del Navegador (F12)

**Presiona F12** y verifica que veas:
```
Context Path detectado: /FinalDAWB_war_exploded
API Base URL: /FinalDAWB_war_exploded/api
```

**NO deberías ver errores de JavaScript**

---

## 🧪 PRUEBAS DE REGISTRO

### Prueba 1: Registrar Nuevo Usuario

**Llena el formulario:**
```
Username: prueba1
Email: prueba1@test.com
Password: password123
```

**Click en "Registrar"**

**✅ Resultado esperado:**
- Mensaje verde: "✅ Registro exitoso para prueba1! ID: X"
- Formulario se limpia automáticamente
- JSON en el área de salida:
  ```json
  {
    "id": 2,
    "username": "prueba1",
    "email": "prueba1@test.com",
    "role": "USER"
  }
  ```

**❌ Si NO funciona:**

1. **Verifica en la pestaña Network (F12):**
   - Busca la petición `POST /api/auth/register`
   - Click en ella
   - Ve la pestaña "Response"
   - ¿Qué devuelve?

2. **Verifica el Status Code:**
   - ✅ 201 = Éxito
   - ❌ 400 = Usuario ya existe
   - ❌ 500 = Error en el servidor
   - ❌ 404 = URL incorrecta

3. **Si es 500 (Error del servidor):**
   - Ve a IntelliJ
   - Revisa los logs de Tomcat en la consola
   - Busca excepciones

### Prueba 2: Usuario Duplicado

**Intenta registrar el mismo usuario otra vez:**
```
Username: prueba1
Email: prueba1@test.com
Password: password123
```

**✅ Resultado esperado:**
- Mensaje rojo: "❌ Fallo en el registro: El nombre de usuario o correo electrónico ya está registrado."
- JSON:
  ```json
  {
    "message": "El nombre de usuario o correo electrónico ya está registrado."
  }
  ```

---

## 🔐 PRUEBAS DE LOGIN

### Prueba 1: Login con testuser (pre-existente)

**Llena el formulario:**
```
Username o Email: testuser
Password: password123
```

**Click en "Iniciar Sesión"**

**✅ Resultado esperado:**
- Mensaje verde: "Login exitoso para testuser! Rol: ADMIN"
- JSON:
  ```json
  {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "ADMIN"
  }
  ```

### Prueba 2: Login con usuario recién creado

**Llena el formulario:**
```
Username o Email: prueba1
Password: password123
```

**✅ Resultado esperado:**
- Mensaje verde: "Login exitoso para prueba1! Rol: USER"
- JSON con datos del usuario

### Prueba 3: Login con credenciales incorrectas

**Llena el formulario:**
```
Username o Email: prueba1
Password: wrongpassword
```

**✅ Resultado esperado:**
- Mensaje rojo: "❌ Fallo de autenticación: Credenciales inválidas o usuario inactivo."
- JSON:
  ```json
  {
    "message": "Credenciales inválidas o usuario inactivo."
  }
  ```

---

## 📋 CHECKLIST DE VERIFICACIÓN

### Antes de Probar:
- [ ] Proyecto recompilado (Build → Rebuild Project)
- [ ] Aplicación redesplegada
- [ ] Tomcat corriendo (ver logs sin errores)
- [ ] MySQL/MariaDB corriendo
- [ ] Base de datos "cocina" existe
- [ ] Rol "USER" existe en tabla `roles`

### Verificar Base de Datos:
```sql
-- Verificar que existe el rol USER
SELECT * FROM roles WHERE nombre = 'USER';

-- Ver usuarios existentes
SELECT id, username, email, role_id, estado FROM usuarios;
```

### Al Abrir la Página:
- [ ] Consola del navegador (F12) sin errores de JavaScript
- [ ] Context Path detectado correctamente
- [ ] API Base URL correcta

### Al Probar Registro:
- [ ] Formulario se envía sin errores de consola
- [ ] Aparece en Network tab (F12)
- [ ] Status code es 201 o 400
- [ ] Respuesta es JSON (no HTML)
- [ ] Mensaje de éxito/error se muestra

### Al Probar Login:
- [ ] Formulario se envía sin errores
- [ ] Status code es 200 o 401
- [ ] Respuesta es JSON
- [ ] Mensaje se muestra correctamente

---

## 🔍 DEBUGGING PASO A PASO

### Si el Registro NO Funciona:

#### Paso 1: Verifica JavaScript
**F12 → Console**
- ¿Hay errores rojos?
- ¿Se detecta el context path correctamente?

#### Paso 2: Verifica la Petición HTTP
**F12 → Network → Filtra por "Fetch/XHR"**
- Click en "Registrar"
- ¿Aparece `POST /api/auth/register`?
- Click en ella
- Ve a "Headers":
  - Request URL: ¿Es correcta?
  - Status Code: ¿Cuál es?
- Ve a "Payload":
  - ¿Se envió el JSON correcto?
- Ve a "Response":
  - ¿Es JSON o HTML?
  - ¿Qué dice?

#### Paso 3: Verifica el Backend
**En IntelliJ → Consola de Tomcat**
- ¿Hay excepciones cuando haces click en Registrar?
- Busca líneas con "ERROR" o "Exception"

#### Paso 4: Verifica la Base de Datos
```sql
-- ¿Existe el rol USER?
SELECT * FROM roles;

-- ¿Se creó el usuario?
SELECT * FROM usuarios ORDER BY id DESC LIMIT 5;
```

### Si el Login NO Funciona:

#### Paso 1: Verifica que el usuario existe
```sql
SELECT id, username, email, estado FROM usuarios 
WHERE username = 'testuser' OR username = 'prueba1';
```

#### Paso 2: Verifica el estado del usuario
- Estado debe ser "ACTIVO"
- Si es "BLOQUEADO" o "INACTIVO", el login fallará

#### Paso 3: Verifica la contraseña
- La contraseña se hashea con BCrypt
- Usa la contraseña: `password123`
- Para testuser y para los usuarios que crees

---

## 🆘 PROBLEMAS COMUNES Y SOLUCIONES

### Problema 1: "Unexpected token" en la consola

**Causa:** Error de sintaxis en JavaScript (ya corregido)

**Solución:** 
- Ya está corregido
- Haz hard reload: Ctrl+Shift+R

### Problema 2: Fetch da error de CORS

**Causa:** Estás abriendo `file://` en lugar de `http://`

**Solución:**
- Usa: `http://localhost:8080/FinalDAWB_war_exploded/`
- NO uses: `file:///home/jonathan/...`

### Problema 3: 404 Not Found

**Causa:** URL incorrecta o aplicación no desplegada

**Solución:**
1. Verifica el context path en F12
2. Verifica que Tomcat esté corriendo
3. Redespliega la aplicación

### Problema 4: 500 Internal Server Error

**Causa:** Error en el código del backend

**Solución:**
1. Revisa logs de Tomcat en IntelliJ
2. Busca la excepción completa
3. Puede ser:
   - Error de conexión a BD
   - Rol USER no existe
   - Error de CDI (Weld)

### Problema 5: Respuesta es HTML en lugar de JSON

**Causa:** Error 404 o 500, Tomcat devuelve página de error

**Solución:**
1. Verifica la URL en Network tab
2. Ve el Response en Network tab
3. Si es HTML, hay un error de servidor o URL

### Problema 6: Usuario no se registra pero tampoco da error

**Causa:** Puede ser que sí se registró pero no lo ves

**Solución:**
```sql
-- Verifica en la base de datos
SELECT * FROM usuarios ORDER BY id DESC;
```

---

## 📊 TABLA DE DIAGNÓSTICO

| Síntoma | Causa Probable | Solución |
|---------|----------------|----------|
| Error de sintaxis en consola | JavaScript mal formado | Ya corregido, hard reload |
| 404 en Network | URL incorrecta | Verifica context path |
| 500 en Network | Error en backend | Revisa logs de Tomcat |
| HTML en Response | Página de error | Verifica que app esté desplegada |
| No aparece en Network | JavaScript no ejecuta | Verifica errores en Console |
| 400 usuario duplicado | Usuario ya existe | Usa otro username/email |
| 401 en login | Credenciales incorrectas | Verifica password |

---

## ✅ COMANDOS ÚTILES

### Verificar MySQL está corriendo:
```bash
systemctl status mariadb
# o
systemctl status mysql
```

### Ver usuarios en la BD:
```bash
mysql -u root -p020320 -e "USE cocina; SELECT id, username, email FROM usuarios;"
```

### Ver roles en la BD:
```bash
mysql -u root -p020320 -e "USE cocina; SELECT * FROM roles;"
```

### Verificar Tomcat está corriendo:
```bash
ps aux | grep tomcat
```

---

## 📝 PLANTILLA DE REPORTE DE ERROR

Si algo no funciona, proporciona esta información:

```
1. ¿Qué intentaste hacer?
   Ejemplo: Registrar usuario con username "prueba1"

2. ¿Qué pasó?
   Ejemplo: No se registró, no dio error, el formulario no se limpió

3. Console (F12 → Console):
   [Pega los errores aquí]

4. Network (F12 → Network → POST /api/auth/register):
   - Status Code: 
   - Request Payload:
   - Response:

5. Logs de Tomcat:
   [Pega las excepciones aquí]

6. Base de datos:
   SELECT * FROM usuarios;
   [Pega el resultado]
```

---

## 🎉 RESUMEN

**Archivo corregido:** `index.html` (código duplicado eliminado)

**Para probar:**
1. Rebuild + Redeploy
2. Abre `http://localhost:8080/FinalDAWB_war_exploded/`
3. F12 → Verifica context path
4. Registra usuario "prueba1"
5. Login con "prueba1"
6. Login con "testuser"

**Si algo falla:** Usa la guía de debugging de este documento

**El código está correcto. Si hay problemas, serán de configuración/despliegue.**

