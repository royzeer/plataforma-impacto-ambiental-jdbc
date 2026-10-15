-- =====================================================================
-- Plataforma Digital Integral para la Gestión de Proyectos de Impacto Ambiental
-- Script de base de datos - Módulo de Gestión de Proyectos
-- Autores: Roycer Cervantes y Sebastián Rodríguez
-- Evidencia: GA7-220501096-AA2-EV01
-- Motor: MySQL 8 (también funciona en MariaDB 10.5+)
-- =====================================================================

-- Creamos la base de datos solo si no existe, con soporte para tildes y ñ
CREATE DATABASE IF NOT EXISTS bd_impacto_ambiental
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE bd_impacto_ambiental;

-- ---------------------------------------------------------------------
-- Tabla: proyecto
-- Guarda cada proyecto de impacto ambiental que se registra en la plataforma.
-- Corresponde a la clase Proyecto del diagrama de clases.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS proyecto (
    id_proyecto     INT            NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(120)   NOT NULL,
    descripcion     VARCHAR(500)   NULL,
    ubicacion       VARCHAR(150)   NOT NULL,
    tipo_impacto    VARCHAR(60)    NOT NULL,
    responsable     VARCHAR(100)   NOT NULL,
    fecha_inicio    DATE           NOT NULL,
    fecha_fin       DATE           NULL,
    presupuesto     DECIMAL(14,2)  NOT NULL DEFAULT 0,
    estado          VARCHAR(20)    NOT NULL DEFAULT 'PLANEADO',
    fecha_registro  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_proyecto PRIMARY KEY (id_proyecto),
    CONSTRAINT uq_proyecto_nombre UNIQUE (nombre),
    CONSTRAINT ck_proyecto_estado CHECK (estado IN ('PLANEADO','EN_EJECUCION','FINALIZADO','CANCELADO')),
    CONSTRAINT ck_proyecto_presupuesto CHECK (presupuesto >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Datos de ejemplo (proyectos reales de la región Caribe que nos sirven para probar)
-- ---------------------------------------------------------------------
INSERT INTO proyecto (nombre, descripcion, ubicacion, tipo_impacto, responsable, fecha_inicio, fecha_fin, presupuesto, estado) VALUES
('Restauración de manglares Ciénaga Grande',
 'Siembra de plántulas de mangle rojo y monitoreo de su crecimiento con la comunidad pesquera.',
 'Ciénaga Grande de Santa Marta, Magdalena', 'RESTAURACION', 'Roycer Cervantes',
 '2026-02-01', '2026-12-15', 45000000.00, 'EN_EJECUCION'),
('Monitoreo de calidad del agua río Manzanares',
 'Toma de muestras mensuales de pH, oxígeno disuelto y coliformes en cinco puntos del río.',
 'Santa Marta, Magdalena', 'AGUA', 'Sebastián Rodríguez',
 '2026-03-10', NULL, 18500000.00, 'EN_EJECUCION'),
('Jornada de limpieza de playas El Rodadero',
 'Recolección y clasificación de residuos sólidos con voluntarios y colegios del sector.',
 'El Rodadero, Santa Marta', 'RESIDUOS', 'Roycer Cervantes',
 '2026-06-05', '2026-06-05', 2300000.00, 'FINALIZADO');
