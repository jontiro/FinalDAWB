-- Script de inicialización de la base de datos
-- Ejecutar este script en MariaDB antes de iniciar la aplicación por primera vez

-- 1. Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS cocina CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cocina;

-- 2. Insertar roles básicos (si no existen)
INSERT IGNORE INTO roles (id, nombre, descripcion, activo, creado_en, actualizado_en)
VALUES
    (1, 'USER', 'Usuario estándar con permisos básicos', true, NOW(), NOW()),
    (2, 'ADMIN', 'Administrador con todos los permisos', true, NOW(), NOW()),
    (3, 'MODERATOR', 'Moderador con permisos especiales', true, NOW(), NOW());

-- 3. Crear un usuario de prueba (password: "password123")
-- El hash es para "password123" generado con BCrypt
INSERT IGNORE INTO usuarios (id, username, email, password_hash, role_id, estado, fecha_registro, fecha_actualizacion)
VALUES
    (1, 'testuser', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'ACTIVO', NOW(), NOW());

-- 4. Crear algunos tags básicos
INSERT IGNORE INTO tags (nombre, descripcion, creado_en, actualizado_en)
VALUES
    ('Vegano', 'Recetas sin productos de origen animal', NOW(), NOW()),
    ('Rápido', 'Recetas que se preparan en menos de 30 minutos', NOW(), NOW()),
    ('Saludable', 'Recetas nutritivas y balanceadas', NOW(), NOW()),
    ('Postre', 'Recetas de postres y dulces', NOW(), NOW()),
    ('Mexicano', 'Recetas de comida mexicana', NOW(), NOW());

-- Verificar que todo se insertó correctamente
SELECT 'Roles insertados:' AS info;
SELECT * FROM roles;

SELECT 'Usuarios de prueba:' AS info;
SELECT id, username, email, role_id, estado FROM usuarios;

SELECT 'Tags disponibles:' AS info;
SELECT * FROM tags;

