-- =====================================================
-- Migración inicial: Sistema de Registro de Pacientes
-- =====================================================

-- 1) ROLES
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(30)  NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2) USUARIOS
CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(120) NOT NULL UNIQUE,
    password        VARCHAR(200) NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3) USUARIO_ROLES (N:M)
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id     BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES roles(id)     ON DELETE CASCADE
);

CREATE INDEX idx_ur_usuario ON usuario_roles(usuario_id);
CREATE INDEX idx_ur_rol     ON usuario_roles(rol_id);

-- 4) PACIENTES
CREATE TABLE pacientes (
    id                       BIGSERIAL PRIMARY KEY,
    rut                      VARCHAR(12)  NOT NULL UNIQUE,
    nombre_completo          VARCHAR(150) NOT NULL,
    fecha_nacimiento         DATE         NOT NULL,
    sexo                     VARCHAR(15)  NOT NULL,
    direccion                VARCHAR(250),
    telefono                 VARCHAR(20),
    email                    VARCHAR(120),
    historial_medico_basico  TEXT,
    created_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_paciente_sexo CHECK (sexo IN ('MASCULINO','FEMENINO','OTRO'))
);

CREATE INDEX idx_paciente_rut    ON pacientes(rut);
CREATE INDEX idx_paciente_email  ON pacientes(email);
CREATE INDEX idx_paciente_nombre ON pacientes(nombre_completo);

-- 5) HISTORIAL CLINICO
CREATE TABLE historial_clinico (
    id              BIGSERIAL PRIMARY KEY,
    paciente_id     BIGINT       NOT NULL,
    medico_id       BIGINT,
    fecha           DATE         NOT NULL,
    motivo_consulta VARCHAR(300) NOT NULL,
    diagnostico     TEXT,
    tratamiento     TEXT,
    observaciones   TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historial_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_historial_medico   FOREIGN KEY (medico_id)   REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE INDEX idx_historial_paciente ON historial_clinico(paciente_id);
CREATE INDEX idx_historial_fecha    ON historial_clinico(fecha);
