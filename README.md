# 🍳 FinalDAWB - Aplicación Web de Recetas con Jakarta EE

Aplicación web empresarial desarrollada con **Jakarta EE 10**, que permite a los usuarios gestionar recetas de cocina, lugares gastronómicos, comentarios y más. Utiliza **Hibernate 6**, **JAX-RS** (REST API), **CDI** (Inyección de Dependencias), **JPA** (Persistencia) y **Bean Validation**.

---

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API REST](#-api-rest)
- [Resolución de Problemas Comunes](#-resolución-de-problemas-comunes)
- [Desarrollo](#-desarrollo)

---

## ✨ Características

### Funcionalidades Principales

- **👤 Gestión de Usuarios**: Registro, login, perfiles con roles (USER, ADMIN, MODERATOR)
- **📝 Recetas**: CRUD completo de recetas con pasos, ingredientes y tags
- **📍 Lugares**: Gestión de lugares gastronómicos con ubicaciones
- **💬 Comentarios**: Sistema polimórfico para comentar recetas y lugares
- **🏷️ Tags**: Categorización de recetas (Vegano, Rápido, Saludable, etc.)
- **⭐ Favoritos**: Los usuarios pueden guardar sus recetas favoritas
- **🔒 Moderación**: Sistema de aprobación de comentarios
- **🔐 Seguridad**: Autenticación con BCrypt, control de acceso por roles

### Características Técnicas

- API RESTful con JAX-RS
- Persistencia JPA con Hibernate 6
- Inyección de Dependencias con CDI (Jakarta Context and Dependency Injection)
- Validación de datos con Bean Validation
- Transacciones JTA
- Arquitectura en capas (Domain, Repository, Service, REST)

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17+ | Lenguaje base |
| **Jakarta EE** | 10.0 | Plataforma empresarial |
| **Hibernate** | 6.4.4.Final | ORM (Object-Relational Mapping) |
| **JAX-RS (Jersey)** | 3.x | API REST |
| **CDI** | 4.0 | Inyección de dependencias |
| **Bean Validation** | 3.0 | Validación de datos |
| **MariaDB/MySQL** | 10.x / 8.x | Base de datos |
| **Maven** | 3.8+ | Gestión de dependencias |
| **Tomcat** | 10.1+ | Servidor de aplicaciones |

---

## 🏗️ Arquitectura

### Capas de la Aplicación

```
┌─────────────────────────────────────────┐
│          REST Resources (JAX-RS)         │  ← API endpoints
├─────────────────────────────────────────┤
│             Services (CDI)               │  ← Lógica de negocio
├─────────────────────────────────────────┤
│          Repositories (JPA)              │  ← Acceso a datos
├─────────────────────────────────────────┤
│           Domain (Entities)              │  ← Modelo de datos
├─────────────────────────────────────────┤
│         Database (MariaDB/MySQL)         │  ← Persistencia
└─────────────────────────────────────────┘
```

### Modelo de Datos Principal

```
Usuario (usuarios)
├── Receta (receta) - creador_id
│   ├── RecetaPaso (receta_paso)
│   ├── RecetaTag (receta_tag)
│   └── Ingrediente (ingrediente)
├── Lugar (lugar) - creador_id
├── Comentario (comentario) - autor_id
│   ├── objeto_id → Objeto (tipo: Receta/Lugar)
│   └── entidad_id (ID específico de receta/lugar)
├── Favorito (favorito)
└── Role (roles)

Objeto (objeto) - Clasificador polimórfico
├── id: 1 → "Receta"
└── id: 2 → "Lugar"

Tag (tags) - Etiquetas para categorización
```

---

## 📦 Requisitos Previos

### Software Necesario

1. **JDK 17 o superior**
   ```bash
   java -version
   # Debe mostrar versión 17 o superior
   ```

2. **Maven 3.8+**
   ```bash
   mvn -version
   ```

3. **MariaDB 10.x o MySQL 8.x**
   ```bash
   mysql --version
   ```

4. **Apache Tomcat 10.1+** (o usar los scripts incluidos)

---

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <tu-repositorio>
cd FinalDAWB
```

### 2. Configurar la Base de Datos

#### a) Crear la Base de Datos

```bash
mysql -u root -p
```

Ejecutar en MySQL/MariaDB:

```sql
CREATE DATABASE IF NOT EXISTS cocina CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### b) Configurar Conexión

Editar `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/cocina?serverTimezone=UTC"/>
<property name="jakarta.persistence.jdbc.user" value="TU_USUARIO"/>
<property name="jakarta.persistence.jdbc.password" value="TU_PASSWORD"/>
```

#### c) Inicializar Datos

El schema se crea automáticamente con Hibernate (`hibernate.hbm2ddl.auto=update`).

Para datos iniciales (roles, usuarios, tags):

```bash
mysql -u root -p cocina < src/main/resources/init-db.sql
```

#### d) Migración: Agregar columna `entidad_id`

**⚠️ IMPORTANTE**: Si la tabla `comentario` ya existe, ejecuta:

```bash
mysql -u root -p cocina < migration-add-entidad-id.sql
```

Esto agrega la columna necesaria para almacenar el ID específico de recetas/lugares comentados.

### 3. Compilar el Proyecto

```bash
mvn clean package
```

Esto genera: `target/FinalDAWB-1.0-SNAPSHOT.war`

### 4. Desplegar en Tomcat

#### Opción A: Script Automático (Local)

```bash
./install-tomcat-quick.sh
```

#### Opción B: Manual

1. Copiar el WAR:
   ```bash
   cp target/FinalDAWB-1.0-SNAPSHOT.war /ruta/a/tomcat/webapps/
   ```

2. Iniciar Tomcat:
   ```bash
   /ruta/a/tomcat/bin/catalina.sh run
   ```

### 5. Verificar Despliegue

- **Frontend**: http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/
- **API REST**: http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/

Endpoint de prueba:
```bash
curl http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/usuarios
```

---

## 📁 Estructura del Proyecto

```
FinalDAWB/
├── src/
│   ├── main/
│   │   ├── java/com/dawb/finaldawb/
│   │   │   ├── domain/              # Entidades JPA
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Receta.java
│   │   │   │   ├── Comentario.java
│   │   │   │   ├── Lugar.java
│   │   │   │   ├── Objeto.java      # Clasificador polimórfico
│   │   │   │   └── ...
│   │   │   ├── repository/          # Acceso a datos
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── RecetaRepository.java
│   │   │   │   ├── ComentarioRepository.java
│   │   │   │   └── ...
│   │   │   ├── service/             # Lógica de negocio
│   │   │   │   ├── UsuarioService.java
│   │   │   │   ├── RecetaService.java
│   │   │   │   ├── ComentarioService.java
│   │   │   │   └── ...
│   │   │   └── rest/                # API REST
│   │   │       ├── UsuarioResource.java
│   │   │       ├── RecetaResource.java
│   │   │       ├── ComentarioResource.java
│   │   │       └── dto/             # DTOs para request/response
│   │   │           ├── ComentarioRequest.java
│   │   │           ├── ComentarioResponse.java
│   │   │           └── ...
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   ├── persistence.xml  # Configuración JPA
│   │   │   │   └── beans.xml        # Configuración CDI
│   │   │   └── init-db.sql          # Script de inicialización
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml          # Descriptor web
│   │       │   └── beans.xml        # CDI para webapp
│   │       ├── index.html
│   │       ├── home.html
│   │       ├── recetas.html
│   │       ├── lugares.html
│   │       └── assets/              # CSS, JS, imágenes
│   └── test/                        # Tests (si existen)
├── pom.xml                          # Dependencias Maven
├── migration-add-entidad-id.sql     # Script de migración
└── README.md                        # Este archivo
```

---

## 🔌 API REST

### Base URL

```
http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api
```

### Endpoints Principales

#### 👤 Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuarios` | Lista todos los usuarios |
| GET | `/usuarios/{id}` | Obtiene un usuario |
| POST | `/usuarios` | Crea un usuario |
| PUT | `/usuarios/{id}` | Actualiza un usuario |
| DELETE | `/usuarios/{id}` | Elimina un usuario |

#### 📝 Recetas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/recetas` | Lista todas las recetas públicas |
| GET | `/recetas/{id}` | Obtiene una receta |
| POST | `/recetas` | Crea una receta |
| PUT | `/recetas/{id}` | Actualiza una receta |
| DELETE | `/recetas/{id}` | Elimina una receta |
| GET | `/recetas/usuario/{userId}` | Recetas de un usuario |

#### 💬 Comentarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/comentarios/{id}` | Obtiene un comentario |
| POST | `/comentarios` | **Crea un comentario** |
| GET | `/comentarios/receta/{recetaId}` | Comentarios de una receta |
| DELETE | `/comentarios/{id}` | Elimina un comentario |

### 💬 Crear Comentario (POST /api/comentarios)

**⚠️ SOLUCIÓN AL PROBLEMA COMÚN**

#### Request Body

```json
{
  "usuarioId": 1,
  "texto": "¡Delicioso!",
  "recetaId": 1
}
```

#### Campos

- **`usuarioId`** (Long, requerido): ID del usuario que comenta
- **`texto`** (String, requerido): Contenido del comentario
- **`recetaId`** (Long, requerido): ID de la receta específica

#### Ejemplo con cURL

```bash
curl -X POST http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/comentarios \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "texto": "¡Delicioso!",
    "recetaId": 1
  }'
```

#### Response (201 Created)

```json
{
  "id": 1,
  "contenido": "¡Delicioso!",
  "autorId": 1,
  "autorUsername": "testuser",
  "objetoId": 1,
  "objetoDescripcion": "Receta",
  "entidadId": 1,
  "moderado": false,
  "fechaCreacion": "2025-11-24T10:30:00Z",
  "fechaActualizacion": "2025-11-24T10:30:00Z"
}
```

#### Errores Posibles

| Código | Error | Causa |
|--------|-------|-------|
| 404 | "Usuario no encontrado" | No existe usuario con ese ID |
| 404 | "Error al crear comentario: la receta o el tipo Objeto no existe" | No existe la receta con ese ID O no existe el tipo "Receta" en la tabla `objeto` |

---

## 🐛 Resolución de Problemas Comunes

### Problema: "El objeto o receta no existe"

#### Síntomas

Al crear un comentario con Insomnia/Postman:

```json
{
  "usuarioId": 1,
  "texto": "Delicioso",
  "recetaId": 1
}
```

Recibes: **404 - "Error al crear comentario: la receta o el tipo Objeto no existe"**

#### Causas Posibles

1. ❌ **No existe el tipo "Receta" en la tabla `objeto`**
2. ❌ **No existe una receta con ID 1 en la tabla `receta`**
3. ❌ **Falta la columna `entidad_id` en la tabla `comentario`**

#### Solución Paso a Paso

##### 1️⃣ Verificar que existe el tipo "Receta" en la tabla `objeto`

```bash
mysql -u root -p cocina -e "SELECT * FROM objeto;"
```

**Debería mostrar:**

```
+----+-------------+---------------------+---------------------+
| id | descripcion | creado_en           | actualizado_en      |
+----+-------------+---------------------+---------------------+
|  1 | Receta      | 2025-11-24 10:00:00 | 2025-11-24 10:00:00 |
|  2 | Lugar       | 2025-11-24 10:00:00 | 2025-11-24 10:00:00 |
+----+-------------+---------------------+---------------------+
```

**Si está vacío**, ejecutar:

```bash
mysql -u root -p cocina < src/main/resources/init-db.sql
```

##### 2️⃣ Verificar que existe una receta con ID 1

```bash
mysql -u root -p cocina -e "SELECT id, titulo FROM receta WHERE id = 1;"
```

**Debería mostrar:**

```
+----+------------------+
| id | titulo           |
+----+------------------+
|  1 | Tacos al Pastor  |
+----+------------------+
```

**Si no existe**, crear una receta manualmente:

```sql
INSERT INTO receta (titulo, descripcion, tiempo_preparacion, creador_id, fecha_creacion, fecha_actualizacion, privacidad)
VALUES ('Tacos al Pastor', 'Deliciosos tacos mexicanos', 45, 1, NOW(), NOW(), 0);
```

O usar el endpoint POST `/api/recetas`.

##### 3️⃣ Verificar que existe la columna `entidad_id` en `comentario`

```bash
mysql -u root -p cocina -e "DESCRIBE comentario;"
```

**Debe incluir:**

```
+--------------------+--------------+------+-----+---------+----------------+
| Field              | Type         | Null | Key | Default | Extra          |
+--------------------+--------------+------+-----+---------+----------------+
| id                 | bigint(20)   | NO   | PRI | NULL    | auto_increment |
| contenido          | longtext     | NO   |     | NULL    |                |
| autor_id           | bigint(20)   | NO   | MUL | NULL    |                |
| objeto_id          | int(11)      | YES  | MUL | NULL    |                |
| entidad_id         | int(11)      | YES  |     | NULL    |     👈 ESTO    |
| moderado           | tinyint(1)   | NO   |     | 0       |                |
| fecha_creacion     | datetime(6)  | NO   |     | NULL    |                |
| fecha_actualizacion| datetime(6)  | NO   |     | NULL    |                |
+--------------------+--------------+------+-----+---------+----------------+
```

**Si falta `entidad_id`**, ejecutar:

```bash
mysql -u root -p cocina < migration-add-entidad-id.sql
```

##### 4️⃣ Reintentar la petición

```bash
curl -X POST http://localhost:8080/FinalDAWB-1.0-SNAPSHOT/api/comentarios \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "texto": "¡Delicioso!",
    "recetaId": 1
  }'
```

**Ahora debería funcionar** ✅

---

### Problema: "Cannot resolve table 'comentario'" (IntelliJ)

#### Causa

IntelliJ no tiene configurada la conexión a la base de datos.

#### Solución

1. **Database Tool Window** (lateral derecho)
2. **+ → Data Source → MariaDB/MySQL**
3. Configurar:
   - Host: `localhost`
   - Port: `3306`
   - Database: `cocina`
   - User: `tu_usuario`
   - Password: `tu_password`
4. **Test Connection** → **Apply** → **OK**

---

### Problema: Error al compilar el proyecto

#### Causa Común

Falta configurar el JDK en Maven.

#### Solución

Verificar `pom.xml`:

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

Limpiar y recompilar:

```bash
mvn clean install -U
```

---

### Problema: "No bean found" al iniciar

#### Causa

CDI no está habilitado.

#### Solución

Verificar que existan estos archivos:

- `src/main/resources/META-INF/beans.xml`
- `src/main/webapp/WEB-INF/beans.xml`

Contenido mínimo:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
       version="4.0" bean-discovery-mode="all">
</beans>
```

---

## 👨‍💻 Desarrollo

### Hot Reload

Para desarrollo con recarga automática:

```bash
mvn clean package
# Copiar WAR a Tomcat webapps/
# Tomcat detectará cambios automáticamente
```

### Logs

Ver logs de Tomcat:

```bash
tail -f /ruta/a/tomcat/logs/catalina.out
```

### Testing con Insomnia/Postman

Importar colecciones incluidas:

- `insomnia_collection.json`
- `insomnia_collection_completa.json`

### Base de Datos de Prueba

Resetear datos:

```bash
mysql -u root -p cocina < src/main/resources/init-db.sql
```

---

## 📝 Notas Adicionales

### Sistema de Comentarios Polimórfico

El diseño permite comentar múltiples tipos de entidades:

1. **Tabla `objeto`**: Define TIPOS de entidades (Receta, Lugar)
2. **Campo `objeto_id`**: FK a la tabla `objeto` (indica el tipo)
3. **Campo `entidad_id`**: ID de la entidad específica (ej: ID de receta)

Ejemplo:

| id | contenido | autor_id | objeto_id | entidad_id | ... |
|----|-----------|----------|-----------|------------|-----|
| 1  | "Rico!"   | 1        | 1 (Receta)| 5          | ... |

Significa: Usuario 1 comentó "Rico!" en la Receta con ID 5.

### Seguridad

- Las contraseñas se almacenan hasheadas con BCrypt
- Usuario de prueba: `testuser` / `password123`
- Los endpoints sensibles requieren validación de roles (implementar en `AuthFilter` si es necesario)

### Próximas Mejoras

- [ ] Implementar JWT para autenticación
- [ ] Agregar endpoints para lugares
- [ ] Sistema de valoraciones (estrellas)
- [ ] Subida de imágenes para recetas
- [ ] Búsqueda avanzada con filtros

---

## 📄 Licencia

[Especifica tu licencia aquí]

---

## 👥 Contribuciones

[Instrucciones para contribuir]

---

## 📧 Contacto

[Tu información de contacto]

---

**¡Disfruta cocinando con FinalDAWB! 🍳👨‍🍳**

