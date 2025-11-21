/*
Esquema: cocina
Objetivo: Mejoras de tipos/constraints, normalización de tags, fechas consistentes, booleans, y realineo de usuario -> usuarios con enums de rol/estado.
Nota: Ejecutar en mantenimiento. Requiere revisar el mapping actual de valores en rol.rol_desc para convertir a enum (ADMIN/USER/VISITOR).
Backup recomendado antes de ejecutar.
*/

-- Seguridad y rendimiento
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE;
SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
START TRANSACTION;

/* =====================
   1) Ajustes en comentario
   ===================== */
-- fecha_creacion: INT -> TIMESTAMP
ALTER TABLE comentario
  MODIFY COLUMN fecha_creacion TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY COLUMN moderado TINYINT(1) NOT NULL DEFAULT 0;
-- Índice sugerido
CREATE INDEX IF NOT EXISTS idx_comentario_autor_fecha ON comentario (autor_id, fecha_creacion);

/* =====================
   2) Ajustes en lugar
   ===================== */
-- id AUTO_INCREMENT si procede
ALTER TABLE lugar
  MODIFY COLUMN id INT(11) NOT NULL AUTO_INCREMENT,
  MODIFY COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

/* =====================
   3) Objeto: ampliar descripcion
   ===================== */
ALTER TABLE objeto
  MODIFY COLUMN descripcion VARCHAR(100) NOT NULL;

/* =====================
   4) Receta: normalizar tags y tipos/fechas
   ===================== */
-- Tabla puente receta_tag (si no existe)
CREATE TABLE IF NOT EXISTS receta_tag (
  receta_id INT NOT NULL,
  tag_id INT NOT NULL,
  PRIMARY KEY (receta_id, tag_id),
  CONSTRAINT receta_tag_receta_fk FOREIGN KEY (receta_id) REFERENCES receta(id) ON DELETE CASCADE,
  CONSTRAINT receta_tag_tag_fk FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Migrar los tags existentes a tabla puente
INSERT IGNORE INTO receta_tag (receta_id, tag_id)
SELECT id AS receta_id, tag_id FROM receta;

-- Ajustes de tipos y fechas
ALTER TABLE receta
  MODIFY COLUMN pasos SMALLINT UNSIGNED NOT NULL,
  MODIFY COLUMN tiempo_preparacion SMALLINT UNSIGNED NOT NULL COMMENT 'minutos',
  MODIFY COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY COLUMN fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  MODIFY COLUMN privacidad TINYINT(1) NOT NULL DEFAULT 0;

-- Quitar tag_id de receta (ya migrado a receta_tag)
ALTER TABLE receta
  DROP FOREIGN KEY receta_tag_id_fk,
  DROP INDEX receta_tag_id_fk,
  DROP COLUMN tag_id;

-- Índices sugeridos
CREATE INDEX IF NOT EXISTS idx_receta_creador_fecha ON receta (creador_id, fecha_creacion);

/* =====================
   5) Recomendacion: tipo y fechas
   ===================== */
ALTER TABLE recomendacion
  MODIFY COLUMN tipo VARCHAR(30) NOT NULL,
  MODIFY COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

/* =====================
   6) Usuario -> usuarios + enums rol/estado + nombres consistentes
   ===================== */
-- Añadir columnas nuevas para valores de enum en texto
ALTER TABLE usuario
  ADD COLUMN rol VARCHAR(20) NULL AFTER password_hash,
  ADD COLUMN estado VARCHAR(20) NULL AFTER rol;

-- Mapear rol_id -> rol (ajusta el mapeo según tus valores en rol.rol_desc)
-- Asume que rol_desc ya contiene valores compatibles con el enum (p.ej., 'ADMIN', 'USER', 'VISITOR').
UPDATE usuario u
JOIN rol r ON r.id = u.rol_id
SET u.rol = UPPER(r.rol_desc)
WHERE u.rol IS NULL;

-- Mapear estado_cuenta -> estado (1=ACTIVO, 0=BLOQUEADO)
UPDATE usuario
SET estado = CASE estado_cuenta WHEN 1 THEN 'ACTIVO' ELSE 'BLOQUEADO' END
WHERE estado IS NULL;

-- Ajustar nombres de columnas para alinear con entidades
ALTER TABLE usuario
  MODIFY COLUMN username VARCHAR(50) NOT NULL,
  MODIFY COLUMN email VARCHAR(120) NOT NULL,
  MODIFY COLUMN password_hash VARCHAR(60) NOT NULL,
  MODIFY COLUMN fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY COLUMN ultima_conexion TIMESTAMP NULL DEFAULT NULL;

-- Eliminar FK a rol y columnas antiguas si ya migraste a enum
ALTER TABLE usuario
  DROP FOREIGN KEY usuario_rol_id_fk,
  DROP INDEX usuario_rol_id_fk,
  DROP COLUMN rol_id,
  DROP COLUMN estado_cuenta;

-- Renombrar columnas a camelCase como en la entidad
ALTER TABLE usuario
  CHANGE COLUMN password_hash passwordHash VARCHAR(60) NOT NULL,
  CHANGE COLUMN fecha_registro fechaRegistro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHANGE COLUMN ultima_conexion ultimaConexion TIMESTAMP NULL DEFAULT NULL;

-- Renombrar tabla a plural
RENAME TABLE usuario TO usuarios;

/* =====================
   7) Rehacer FKs hacia usuarios
   ===================== */
-- comentario.autor_id
ALTER TABLE comentario
  DROP FOREIGN KEY comentario_usuario_id_fk,
  DROP INDEX comentario_usuario_id_fk;
ALTER TABLE comentario
  ADD CONSTRAINT comentario_usuario_id_fk FOREIGN KEY (autor_id) REFERENCES usuarios(id);

-- lugar.autor_id
ALTER TABLE lugar
  DROP FOREIGN KEY lugar_usuario_id_fk,
  DROP INDEX lugar_usuario_id_fk;
ALTER TABLE lugar
  ADD CONSTRAINT lugar_usuario_id_fk FOREIGN KEY (autor_id) REFERENCES usuarios(id);

-- receta.creador_id
ALTER TABLE receta
  DROP FOREIGN KEY receta_usuario_id_fk,
  DROP INDEX receta_usuario_id_fk;
ALTER TABLE receta
  ADD CONSTRAINT receta_usuario_id_fk FOREIGN KEY (creador_id) REFERENCES usuarios(id);

-- recomendacion.autor_id
ALTER TABLE recomendacion
  DROP FOREIGN KEY recomendacion_usuario_id_fk,
  DROP INDEX recomendacion_usuario_id_fk;
ALTER TABLE recomendacion
  ADD CONSTRAINT recomendacion_usuario_id_fk FOREIGN KEY (autor_id) REFERENCES usuarios(id);

/* =====================
   8) Roles: opcional, si vas a mantener entidad Role
   ===================== */
-- Crear tabla roles (si decides usar entidad Role en vez de enum en usuarios)
CREATE TABLE IF NOT EXISTS roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(30) NOT NULL,
  descripcion VARCHAR(200) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- (Opcional) Poblar desde rol existente
INSERT IGNORE INTO roles (nombre)
SELECT DISTINCT UPPER(rol_desc) FROM rol;

-- (Opcional) Puedes eliminar la tabla antigua 'rol' si ya no se usa
-- DROP TABLE rol;

/* =====================
   9) Índices y unicidad
   ===================== */
ALTER TABLE tag
  ADD UNIQUE KEY IF NOT EXISTS uk_tag_nombre (nombre);

ALTER TABLE usuarios
  ADD UNIQUE KEY IF NOT EXISTS uk_usuario_username (username),
  ADD UNIQUE KEY IF NOT EXISTS uk_usuario_email (email);

COMMIT;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
