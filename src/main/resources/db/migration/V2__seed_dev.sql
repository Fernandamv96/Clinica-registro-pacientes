-- =====================================================
-- Datos seed para desarrollo (NO usar en producción)
-- =====================================================

-- Usuario médico de ejemplo
-- Password: Medico123! (BCrypt)
INSERT INTO usuarios (username, email, password, nombre_completo, activo, created_at, updated_at)
VALUES ('jperez', 'jperez@clinica.cl',
        '$2a$12$AbCdEfGhIjKlMnOpQrStUvWxYz0123456789AbCdEfGhIjKlMnOpQrStU',
        'Dr. Juan Pérez', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Usuario recepcionista
INSERT INTO usuarios (username, email, password, nombre_completo, activo, created_at, updated_at)
VALUES ('mgarcia', 'mgarcia@clinica.cl',
        '$2a$12$AbCdEfGhIjKlMnOpQrStUvWxYz0123456789AbCdEfGhIjKlMnOpQrStU',
        'María García', true, NOW(), NOW())
ON CONFLICT DO NOTHING;
