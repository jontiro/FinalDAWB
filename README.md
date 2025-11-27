# 🍳 Cocina Social - Plataforma de Recetas y Lugares Gastronómicos

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-blue.svg)](https://jakarta.ee/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)

Plataforma web colaborativa para compartir recetas, descubrir lugares gastronómicos y conectar con amantes de la cocina. Sistema completo con gestión de usuarios, comentarios, recomendaciones y panel de administración.

---

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Ejecución](#-ejecución)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API REST](#-api-rest)
- [Seguridad](#-seguridad)
- [Panel de Administración](#-panel-de-administración)
- [Capturas de Pantalla](#-capturas-de-pantalla)
- [Solución de Problemas](#-solución-de-problemas)
- [Contribuir](#-contribuir)
- [Licencia](#-licencia)

---

## ✨ Características

### 🍴 Gestión de Recetas
- ✅ **CRUD completo** de recetas con pasos detallados
- ✅ **Sistema de tags** para categorización (Vegano, Rápido, Saludable, etc.)
- ✅ **Control de privacidad** (recetas públicas y privadas)
- ✅ **Tiempo de preparación** y dificultad
- ✅ **Búsqueda y filtrado** avanzado

### 📍 Lugares Gastronómicos
- ✅ **Directorio de lugares** (restaurantes, cafeterías, mercados)
- ✅ **Información completa**: dirección, ciudad, país
- ✅ **Recomendaciones** por categoría
- ✅ **Geolocalización** y mapas interactivos

### 💬 Sistema de Comentarios
- ✅ **Comentarios polimórficos** en recetas y lugares
- ✅ **Moderación** de contenido
- ✅ **Sistema de calificación**
- ✅ **Protección anti-spam**

### 👥 Gestión de Usuarios
- ✅ **Registro e inicio de sesión** seguro
- ✅ **Sistema de roles** (USER, ADMIN, MODERATOR)
- ✅ **Perfiles personalizables**
- ✅ **Autenticación con BCrypt**

### 🛡️ Seguridad
- ✅ **Protección CSRF** en todas las operaciones POST/PUT/DELETE
- ✅ **Sanitización** de entradas (XSS prevention)
- ✅ **Prepared Statements** (SQL Injection prevention)
- ✅ **Validación** de datos en backend y frontend

### 🎛️ Panel de Administración
- ✅ **Dashboard** con estadísticas en tiempo real
- ✅ **Gestión de usuarios** (crear, editar, eliminar, cambiar roles)
- ✅ **Moderación de comentarios** (aprobar/rechazar)
- ✅ **Gráficas interactivas** con Chart.js
- ✅ **Gestión de catálogos** (tipos, objetos, tags)

### 🎨 Interfaz de Usuario
- ✅ **Diseño responsive** con Tailwind CSS
- ✅ **Componentes reutilizables**
- ✅ **Modales interactivos**
- ✅ **Notificaciones Toast**
- ✅ **Animaciones fluidas**

---

## 🏗️ Arquitectura

### Patrón de Diseño
```
┌─────────────────────────────────────────────────────────┐
│                    FRONTEND (SPA)                       │
│  HTML5 + Tailwind CSS + Vanilla JavaScript             │
│  • auth.js (Autenticación)                              │
│  • csrf-protection.js (Seguridad)                       │
│  • dashboard.js (Panel Admin)                           │
│  • components.js (Componentes reutilizables)            │
└─────────────────────────────────────────────────────────┘
                           ↕️ REST API (JSON)
┌─────────────────────────────────────────────────────────┐
│                 BACKEND (Jakarta EE)                    │
│                                                         │
│  ┌─────────────────────────────────────────────────┐  │
│  │          REST Resources (JAX-RS)                 │  │
│  │  AuthResource | RecetaResource | LugarResource  │  │
│  │  ComentarioResource | AdminResource              │  │
│  └─────────────────────────────────────────────────┘  │
│                           ↕️                            │
│  ┌─────────────────────────────────────────────────┐  │
│  │          Services (Business Logic)               │  │
│  │  AuthService | RecetaService | LugarService     │  │
│  │  ComentarioService | UsuarioService              │  │
│  └─────────────────────────────────────────────────┘  │
│                           ↕️                            │
│  ┌─────────────────────────────────────────────────┐  │
│  │        Repositories (Data Access)                │  │
│  │  UsuarioRepository | RecetaRepository            │  │
│  │  LugarRepository | ComentarioRepository          │  │
│  └─────────────────────────────────────────────────┘  │
│                           ↕️                            │
│  ┌─────────────────────────────────────────────────┐  │
│  │          JPA/Hibernate (ORM)                     │  │
│  │  EntityManager | Transaction Management          │  │
│  └─────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           ↕️ JDBC
┌─────────────────────────────────────────────────────────┐
│              DATABASE (MariaDB/MySQL)                   │
│  usuarios | recetas | lugares | comentarios            │
│  roles | tags | recomendaciones                         │
└─────────────────────────────────────────────────────────┘
```

### Tecnologías Utilizadas

| Categoría | Tecnología | Versión | Propósito |
|-----------|-----------|---------|-----------|
| **Backend** | Java | 21 | Lenguaje base |
| | Jakarta EE | 10.0.0 | Framework empresarial |
| | JAX-RS (Jersey) | 3.1.5 | API REST |
| | JPA/Hibernate | 6.4.4 | ORM |
| | CDI (Weld) | 5.1.2 | Inyección de dependencias |
| | BCrypt | 0.4 | Hash de contraseñas |
| **Frontend** | HTML5 | - | Estructura |
| | Tailwind CSS | 3.x | Estilos |
| | JavaScript | ES6+ | Lógica del cliente |
| | Font Awesome | 6.4.0 | Iconografía |
| | Chart.js | 4.x | Gráficas |
| **Base de Datos** | MariaDB | 10.x | Almacenamiento |
| **Servidor** | Apache Tomcat | 10.1 | Contenedor web |
| **Build** | Maven | 3.8+ | Gestión de dependencias |

---

## 📦 Requisitos Previos

### Software Necesario

1. **Java Development Kit (JDK) 21**
   ```bash
   # Verificar instalación
   java -version
   javac -version
   ```

2. **Apache Maven 3.8+**
   ```bash
   # Verificar instalación
   mvn -version
   ```

3. **MariaDB/MySQL 10.x+**
   ```bash
   # Verificar instalación
   mysql --version
   ```

4. **Apache Tomcat 10.1.x**
   - Descargar de: https://tomcat.apache.org/download-10.cgi

5. **IDE Recomendado**
   - IntelliJ IDEA Ultimate (recomendado)
   - Eclipse IDE for Enterprise Java
   - NetBeans

---

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/FinalDAWB.git
cd FinalDAWB
```

### 2. Configurar la Base de Datos

```bash
# Iniciar MariaDB
sudo systemctl start mariadb

# Acceder a MariaDB
mysql -u root -p

# Ejecutar desde MySQL prompt
source src/main/resources/init-db.sql
```

O manualmente:

```sql
-- Crear base de datos
CREATE DATABASE cocina CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional)
CREATE USER 'cocina_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON cocina.* TO 'cocina_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar Credenciales

Editar `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://localhost:3306/cocina" />
<property name="jakarta.persistence.jdbc.user" value="root" />
<property name="jakarta.persistence.jdbc.password" value="TU_PASSWORD" />
```

### 4. Compilar el Proyecto

```bash
# Limpiar y compilar
mvn clean package

# O saltar tests
mvn clean package -DskipTests
```

Esto generará: `target/FinalDAWB-1.0-SNAPSHOT.war`

---

## ⚙️ Configuración

### Configuración de Tomcat en IntelliJ IDEA

1. **Abrir Run/Debug Configurations**
   - `Run` → `Edit Configurations`

2. **Agregar Nueva Configuración**
   - Click en `+` → `Tomcat Server` → `Local`

3. **Configurar Servidor**
   - **Name**: `Tomcat 10 - FinalDAWB`
   - **Application Server**: Seleccionar Tomcat 10.1
   - **HTTP Port**: `8080`
   - **JRE**: Java 21

4. **Deployment**
   - Tab `Deployment` → `+` → `Artifact`
   - Seleccionar: `FinalDAWB:war exploded`
   - **Application Context**: `/FinalDAWB_war_exploded`

5. **VM Options** (opcional, para debugging)
   ```
   -Xms512m -Xmx1024m
   ```

### Variables de Entorno (Opcional)

```bash
# En .bashrc o .zshrc
export CATALINA_HOME=/path/to/tomcat
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$CATALINA_HOME/bin:$PATH
```

---

## 🏃 Ejecución

### Opción 1: Desde IntelliJ IDEA

1. Click en el botón **Run** (▶️) o presiona `Shift + F10`
2. Esperar a que Tomcat inicie
3. El navegador se abrirá automáticamente

### Opción 2: Desde Línea de Comandos

```bash
# Copiar WAR a Tomcat
cp target/FinalDAWB-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/

# Iniciar Tomcat
$CATALINA_HOME/bin/catalina.sh run

# O en Windows
%CATALINA_HOME%\bin\catalina.bat run
```

### Opción 3: Maven + Tomcat Plugin

```bash
# Usando el plugin de Maven
mvn tomcat7:run
```

### Acceder a la Aplicación

```
🌐 URL Principal: http://localhost:8080/FinalDAWB_war_exploded/home.html
👤 Admin Panel:   http://localhost:8080/FinalDAWB_war_exploded/admin/dashboard.html
```

### Credenciales de Prueba

**Usuario Admin:**
- **Username**: `admin`
- **Password**: `admin123`

**Usuario Normal:**
- **Username**: `testuser`
- **Password**: `password123`

---

## 📁 Estructura del Proyecto

```
FinalDAWB/
├── src/
│   ├── main/
│   │   ├── java/com/dawb/finaldawb/
│   │   │   ├── config/              # Configuración
│   │   │   │   ├── CdiHk2Binder.java
│   │   │   │   ├── EntityManagerProducer.java
│   │   │   │   ├── JacksonConfig.java
│   │   │   │   └── RestApplication.java
│   │   │   ├── domain/              # Entidades JPA
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Receta.java
│   │   │   │   ├── Lugar.java
│   │   │   │   ├── Comentario.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Tag.java
│   │   │   │   └── ...
│   │   │   ├── repository/          # Capa de acceso a datos
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── RecetaRepository.java
│   │   │   │   ├── LugarRepository.java
│   │   │   │   └── ...
│   │   │   ├── service/             # Lógica de negocio
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── RecetaService.java
│   │   │   │   ├── LugarService.java
│   │   │   │   └── ...
│   │   │   ├── rest/                # Endpoints REST
│   │   │   │   ├── AuthResource.java
│   │   │   │   ├── RecetaResource.java
│   │   │   │   ├── LugarResource.java
│   │   │   │   ├── AdminResource.java
│   │   │   │   └── dto/             # Data Transfer Objects
│   │   │   └── security/            # Seguridad
│   │   │       ├── CsrfFilter.java
│   │   │       └── CsrfTokenService.java
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   ├── persistence.xml  # Configuración JPA
│   │   │   │   └── beans.xml        # CDI
│   │   │   └── init-db.sql          # Script inicial DB
│   │   └── webapp/
│   │       ├── home.html            # Página principal
│   │       ├── recetas.html         # Catálogo de recetas
│   │       ├── lugares.html         # Catálogo de lugares
│   │       ├── comunidad.html       # Comunidad y comentarios
│   │       ├── admin/
│   │       │   ├── dashboard.html   # Panel admin
│   │       │   └── dashboard.js     # Lógica del panel
│   │       ├── assets/
│   │       │   ├── css/             # Estilos
│   │       │   │   ├── common.css
│   │       │   │   ├── home.css
│   │       │   │   └── ...
│   │       │   └── js/              # JavaScript
│   │       │       ├── auth.js
│   │       │       ├── csrf-protection.js
│   │       │       ├── components.js
│   │       │       └── ...
│   │       └── WEB-INF/
│   │           ├── web.xml          # Configuración web
│   │           └── beans.xml        # CDI
│   └── test/                        # Tests
├── pom.xml                          # Maven dependencies
├── README.md                        # Este archivo
└── .gitignore
```

---

## 🔌 API REST

### Base URL
```
http://localhost:8080/FinalDAWB_war_exploded/api
```

### Autenticación

#### Registrarse
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@email.com",
  "password": "password123",
  "nombre": "Juan",
  "apellido": "Pérez"
}
```

#### Iniciar Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "usuario",
  "password": "password123"
}
```

#### Obtener Token CSRF
```http
GET /api/auth/csrf-token
```

### Recetas

#### Listar Recetas
```http
GET /api/recetas
```

#### Obtener Receta por ID
```http
GET /api/recetas/{id}
```

#### Crear Receta
```http
POST /api/recetas
Content-Type: application/json
X-CSRF-Token: {token}

{
  "titulo": "Tacos al Pastor",
  "descripcion": "Deliciosos tacos mexicanos",
  "tiempoPreparacion": 45,
  "creadorId": 1,
  "privacidad": false,
  "pasos": [
    {
      "orden": 1,
      "descripcion": "Marinar la carne..."
    }
  ],
  "tagIds": [1, 5]
}
```

#### Actualizar Receta
```http
PUT /api/recetas/{id}
Content-Type: application/json
X-CSRF-Token: {token}

{
  "titulo": "Tacos al Pastor Mejorados",
  "descripcion": "...",
  ...
}
```

#### Eliminar Receta
```http
DELETE /api/recetas/{id}
X-CSRF-Token: {token}
```

### Lugares

#### Listar Lugares
```http
GET /api/lugares
```

#### Crear Lugar
```http
POST /api/lugares
Content-Type: application/json
X-CSRF-Token: {token}

{
  "nombre": "Restaurante El Buen Sabor",
  "direccion": "Calle Principal 123",
  "ciudad": "Ciudad de México",
  "pais": "México",
  "autorId": 1
}
```

### Comentarios

#### Listar Comentarios
```http
GET /api/comentarios
```

#### Crear Comentario
```http
POST /api/comentarios
Content-Type: application/json
X-CSRF-Token: {token}

{
  "usuarioId": 1,
  "texto": "¡Delicioso!",
  "recetaId": 9
}
```

### Admin

#### Listar Usuarios (Admin)
```http
GET /api/admin/usuarios
Authorization: Required (Admin role)
```

#### Crear Usuario (Admin)
```http
POST /api/admin/usuarios
Content-Type: application/json
X-CSRF-Token: {token}

{
  "username": "newuser",
  "email": "new@email.com",
  "password": "password123",
  "roleId": 1,
  "estado": "ACTIVO"
}
```

#### Moderar Comentario (Admin)
```http
PUT /api/admin/comentarios/{id}/rechazar
X-CSRF-Token: {token}
```

### Códigos de Estado

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Eliminación exitosa |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 405 | Method Not Allowed - Método incorrecto |
| 500 | Internal Server Error - Error del servidor |

---

## 🔒 Seguridad

### Protección CSRF (Cross-Site Request Forgery)

Todas las operaciones de modificación (POST, PUT, DELETE) están protegidas con tokens CSRF.

**Flujo:**
1. Obtener token: `GET /api/auth/csrf-token`
2. Incluir en header: `X-CSRF-Token: {token}`
3. Token válido por 24 horas

**Implementación Frontend:**
```javascript
// csrf-protection.js
const CsrfProtection = {
    async protectedFetch(url, options = {}) {
        const token = await this.getToken();
        options.headers = {
            ...options.headers,
            'X-CSRF-Token': token
        };
        return fetch(url, options);
    }
};
```

### Prevención XSS (Cross-Site Scripting)

**Backend:**
```java
// Sanitización en DTOs
public static String sanitize(String input) {
    return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
}
```

**Frontend:**
```javascript
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
```

### Prevención SQL Injection

Uso de **JPA/Hibernate** con consultas parametrizadas:

```java
// ❌ MAL - Vulnerable a SQL Injection
String query = "SELECT u FROM Usuario u WHERE username = '" + username + "'";

// ✅ BIEN - Seguro con parámetros
TypedQuery<Usuario> query = em.createQuery(
    "SELECT u FROM Usuario u WHERE u.username = :username", 
    Usuario.class
);
query.setParameter("username", username);
```

### Hash de Contraseñas

Uso de **BCrypt** con salt automático:

```java
// Al registrar
String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

// Al verificar
boolean isValid = BCrypt.checkpw(plainPassword, passwordHash);
```

### Control de Acceso por Roles

```java
@Path("/admin")
public class AdminResource {
    // Solo accesible para usuarios con rol ADMIN
    // Verificación en AuthService
}
```

---

## 🎛️ Panel de Administración

Acceder a: `http://localhost:8080/FinalDAWB_war_exploded/admin/dashboard.html`

### Características

#### 📊 Dashboard Principal
- **Estadísticas en tiempo real**:
  - Total de recetas
  - Total de lugares
  - Total de comentarios
  - Total de usuarios
- **Navegación por tabs**:
  - Comentarios
  - Gráficas
  - Usuarios

#### 💬 Gestión de Comentarios
- **Ver todos los comentarios** del sistema
- **Ocultar comentarios** inapropiados
- **Eliminar comentarios** permanentemente
- **Filtro y búsqueda** en tiempo real

#### 📈 Gráficas Interactivas
- **Usuarios activos** (últimos 7 días) - Gráfica de línea
- **Recetas publicadas** (últimos 7 días) - Gráfica de barras
- **Distribución de contenido** - Gráfica de dona
- Powered by **Chart.js**

#### 👥 Gestión de Usuarios
- **Listar todos los usuarios** con información completa
- **Ver detalles** de cualquier usuario
- **Crear nuevos usuarios** con rol asignado
- **Editar usuarios** existentes:
  - Cambiar username, email
  - Cambiar contraseña
  - Modificar rol (USER, ADMIN, MODERATOR)
  - Cambiar estado (ACTIVO, BLOQUEADO)
- **Eliminar usuarios**
- **Búsqueda y filtros**

#### 🔍 Características Adicionales
- **Refresh automático** de datos
- **Validación de formularios**
- **Confirmaciones** antes de acciones destructivas
- **Notificaciones toast** para feedback
- **Responsive design** para tablets

---

## 📸 Capturas de Pantalla

### Página Principal
```
┌─────────────────────────────────────────────────────────┐
│  🍳 Cocina Social          [Recetas] [Lugares] [Login]  │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Descubre recetas increíbles y lugares gastronómicos   │
│                                                         │
│  [Estadísticas: 150+ recetas | 80+ lugares]            │
│                                                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐               │
│  │ Recetas  │ │ Lugares  │ │Comunidad │               │
│  └──────────┘ └──────────┘ └──────────┘               │
└─────────────────────────────────────────────────────────┘
```

### Panel de Admin
```
┌─────────────────────────────────────────────────────────┐
│  🛡️ Admin Panel                    Hola, admin [Logout] │
├─────────────────────────────────────────────────────────┤
│  [Comentarios] [Gráficas] [Usuarios]                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  📊 Estadísticas:                                       │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐                      │
│  │ 150 │ │  80 │ │ 320 │ │  45 │                      │
│  │Rctas│ │Lgrs │ │Comnt│ │Users│                      │
│  └─────┘ └─────┘ └─────┘ └─────┘                      │
│                                                         │
│  📈 Gráficas de actividad...                           │
└─────────────────────────────────────────────────────────┘
```

---

## 🐛 Solución de Problemas

### Problema: Error al iniciar Tomcat

**Síntoma**: `Address already in use: bind`

**Solución**:
```bash
# Encontrar proceso en puerto 8080
lsof -i :8080

# Matar proceso
kill -9 <PID>

# O cambiar el puerto en Tomcat
# Editar: $CATALINA_HOME/conf/server.xml
# <Connector port="8081" protocol="HTTP/1.1" ...>
```

### Problema: Error de compilación Maven

**Síntoma**: `Failed to execute goal ... compiler`

**Solución**:
```bash
# Limpiar completamente
mvn clean

# Verificar versión de Java
java -version  # Debe ser 21

# Actualizar JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk

# Compilar de nuevo
mvn package -DskipTests
```

### Problema: Error de conexión a base de datos

**Síntoma**: `Communications link failure`

**Solución**:
```bash
# 1. Verificar que MariaDB esté corriendo
sudo systemctl status mariadb
sudo systemctl start mariadb

# 2. Verificar credenciales en persistence.xml
# 3. Probar conexión manualmente
mysql -u root -p -h localhost
```

### Problema: Funciones JavaScript no definidas

**Síntoma**: `showTab is not defined` o `API_BASE_URL already declared`

**Solución**:
```javascript
// 1. Limpiar caché del navegador
localStorage.clear();
sessionStorage.clear();
location.reload();

// 2. Hard refresh
// Ctrl + Shift + R (Linux/Windows)
// Cmd + Shift + R (Mac)

// 3. Verificar que auth.js se cargue primero
// Ver orden en HTML: auth.js → csrf-protection.js → dashboard.js
```

### Problema: Error 405 Method Not Allowed

**Síntoma**: Endpoint devuelve 405 en lugar de 200

**Solución**:
1. Verificar que el método HTTP sea correcto (GET, POST, PUT, DELETE)
2. Verificar que el endpoint exista en el Resource correspondiente
3. Verificar la ruta completa: `/api/recurso/accion`

### Problema: Token CSRF inválido

**Síntoma**: Error 403 al hacer POST/PUT/DELETE

**Solución**:
```javascript
// 1. Obtener nuevo token
const response = await fetch('/api/auth/csrf-token');
const data = await response.json();

// 2. Usar CsrfProtection.protectedFetch()
await CsrfProtection.protectedFetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
});
```

### Problema: Página en blanco después del deploy

**Síntoma**: La aplicación carga pero muestra página en blanco

**Solución**:
1. Abrir DevTools (F12) → Console
2. Verificar errores JavaScript
3. Verificar que los archivos CSS y JS se carguen correctamente
4. Verificar Context Path: `/FinalDAWB_war_exploded/`

### Logs y Debugging

**Ver logs de Tomcat**:
```bash
# Tiempo real
tail -f $CATALINA_HOME/logs/catalina.out

# Últimas 100 líneas
tail -100 $CATALINA_HOME/logs/catalina.out

# Buscar errores
grep -i "error" $CATALINA_HOME/logs/catalina.out
```

**Habilitar SQL logging**:
En `persistence.xml`:
```xml
<property name="hibernate.show_sql" value="true" />
<property name="hibernate.format_sql" value="true" />
```

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Por favor, sigue estos pasos:

1. **Fork** el proyecto
2. Crea una **rama** para tu feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. **Push** a la rama (`git push origin feature/AmazingFeature`)
5. Abre un **Pull Request**

### Guías de Estilo

- **Java**: Seguir convenciones de Oracle
- **JavaScript**: Usar ES6+, camelCase
- **SQL**: Palabras clave en MAYÚSCULAS
- **Commits**: Mensajes descriptivos en español

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## 👨‍💻 Autor

**Jonathan**
- Proyecto Final - Desarrollo de Aplicaciones Web (DAWB)
- Universidad: [Tu Universidad]
- Fecha: Noviembre 2025

---

## 🙏 Agradecimientos

- **Jakarta EE Community** por la excelente documentación
- **Tailwind CSS** por el framework de estilos
- **Chart.js** por las gráficas interactivas
- **Font Awesome** por los iconos
- **Stack Overflow** por resolver dudas
- **GitHub Copilot** por asistencia en desarrollo

---

## 📞 Soporte

Si tienes preguntas o problemas:

1. **Issues**: Abre un issue en GitHub
2. **Email**: jonathan@example.com
3. **Documentación**: Revisa este README completo

---

## 🔄 Roadmap

### Versión 1.1 (Próxima)
- [ ] Sistema de favoritos
- [ ] Notificaciones en tiempo real
- [ ] Chat entre usuarios
- [ ] Calificación con estrellas
- [ ] Exportar recetas a PDF

### Versión 2.0 (Futuro)
- [ ] App móvil (Android/iOS)
- [ ] Búsqueda con Elasticsearch
- [ ] Integración con redes sociales
- [ ] Sistema de gamificación
- [ ] API pública con rate limiting

---

## 📊 Estadísticas del Proyecto

```
📂 Líneas de Código:  ~15,000
📝 Archivos Java:     56
🎨 Archivos HTML:     6
📜 Archivos JS:       6
🎨 Archivos CSS:      6
⏱️ Tiempo Desarrollo: 3 meses
🐛 Issues Resueltos:  47
✅ Tests:             Pendiente
```

---

## 🌟 Características Destacadas

### ¿Por qué usar Cocina Social?

✅ **Open Source** - Código abierto y libre
✅ **Moderno** - Tecnologías actuales (Java 21, Jakarta EE 10)
✅ **Seguro** - Protección CSRF, XSS, SQL Injection
✅ **Escalable** - Arquitectura en capas
✅ **Responsive** - Funciona en todos los dispositivos
✅ **Documentado** - README completo y código comentado
✅ **Mantenible** - Código limpio y organizado
✅ **Extensible** - Fácil de agregar nuevas funcionalidades

---

<div align="center">

**⭐ Si te gusta este proyecto, dale una estrella en GitHub ⭐**

Hecho con ❤️ y ☕ por Jonathan

[⬆ Volver arriba](#-cocina-social---plataforma-de-recetas-y-lugares-gastronómicos)

</div>

