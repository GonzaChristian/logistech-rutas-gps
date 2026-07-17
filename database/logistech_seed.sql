-- LOGISTECH - Script MySQL para crear base de datos y datos de prueba.
-- Proyecto: Sistema de Automatizacion y Trazabilidad de Rutas GPS.
-- Uso sugerido:
--   mysql -u root -p < database/logistech_seed.sql

CREATE DATABASE IF NOT EXISTS logistech_rutas
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE logistech_rutas;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS incidencias;
DROP TABLE IF EXISTS recorridos_gps;
DROP TABLE IF EXISTS asignaciones_ruta;
DROP TABLE IF EXISTS rutas;
DROP TABLE IF EXISTS vehiculos;
DROP TABLE IF EXISTS conductores;
DROP TABLE IF EXISTS usuarios;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuarios (
  id_usuario BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL,
  contrasena VARCHAR(100) NOT NULL,
  rol VARCHAR(30) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uk_usuarios_correo (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE conductores (
  id_conductor BIGINT NOT NULL AUTO_INCREMENT,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  dni VARCHAR(15) NOT NULL,
  licencia VARCHAR(30) NOT NULL,
  telefono VARCHAR(20) NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_conductor),
  UNIQUE KEY uk_conductores_dni (dni)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE vehiculos (
  id_vehiculo BIGINT NOT NULL AUTO_INCREMENT,
  placa VARCHAR(15) NOT NULL,
  marca VARCHAR(50) NULL,
  modelo VARCHAR(50) NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_vehiculo),
  UNIQUE KEY uk_vehiculos_placa (placa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rutas (
  id_ruta BIGINT NOT NULL AUTO_INCREMENT,
  origen VARCHAR(150) NOT NULL,
  destino VARCHAR(150) NOT NULL,
  fecha_programada DATE NOT NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_ruta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE asignaciones_ruta (
  id_asignacion BIGINT NOT NULL AUTO_INCREMENT,
  id_ruta BIGINT NOT NULL,
  id_conductor BIGINT NOT NULL,
  id_vehiculo BIGINT NOT NULL,
  fecha_asignacion DATE NOT NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_asignacion),
  KEY idx_asignaciones_ruta_ruta (id_ruta),
  KEY idx_asignaciones_ruta_conductor (id_conductor),
  KEY idx_asignaciones_ruta_vehiculo (id_vehiculo),
  CONSTRAINT fk_asignacion_ruta
    FOREIGN KEY (id_ruta) REFERENCES rutas (id_ruta),
  CONSTRAINT fk_asignacion_conductor
    FOREIGN KEY (id_conductor) REFERENCES conductores (id_conductor),
  CONSTRAINT fk_asignacion_vehiculo
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos (id_vehiculo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE recorridos_gps (
  id_gps BIGINT NOT NULL AUTO_INCREMENT,
  id_asignacion BIGINT NOT NULL,
  latitud DECIMAL(10,8) NOT NULL,
  longitud DECIMAL(11,8) NOT NULL,
  fecha_hora DATETIME(6) NOT NULL,
  PRIMARY KEY (id_gps),
  KEY idx_recorridos_gps_asignacion (id_asignacion),
  CONSTRAINT fk_gps_asignacion
    FOREIGN KEY (id_asignacion) REFERENCES asignaciones_ruta (id_asignacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incidencias (
  id_incidencia BIGINT NOT NULL AUTO_INCREMENT,
  id_asignacion BIGINT NOT NULL,
  tipo VARCHAR(50) NOT NULL,
  descripcion VARCHAR(250) NULL,
  fecha_hora DATETIME(6) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_incidencia),
  KEY idx_incidencias_asignacion (id_asignacion),
  CONSTRAINT fk_incidencia_asignacion
    FOREIGN KEY (id_asignacion) REFERENCES asignaciones_ruta (id_asignacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO usuarios (id_usuario, nombre, correo, contrasena, rol, estado) VALUES
(1, 'Administrador LOGISTECH', 'admin@logistech.local', '$2a$10$Wn60UE1Y3RVwhW62o7Spb.MH/6xGqNHYTRJQI/da/UQkxu6bZGb0y', 'ADMIN', 'ACTIVO'),
(2, 'Supervisor Logistico', 'supervisor@logistech.local', '$2a$10$xmU34/I2uRohKG6ueWl6bOQXtAZAL5nzvkMYADOMZjs3CIoMxQyHy', 'SUPERVISOR', 'ACTIVO');

INSERT INTO conductores (id_conductor, nombres, apellidos, dni, licencia, telefono, estado) VALUES
(1, 'Carlos Alberto', 'Mendoza Rojas', '45879612', 'AIIIC-4587', '987654321', 'ACTIVO'),
(2, 'Rosa Maria', 'Quispe Huaman', '47236589', 'AIIIB-4723', '956321478', 'ACTIVO'),
(3, 'Jorge Luis', 'Salazar Torres', '42157896', 'AIIIC-4215', '945678123', 'ACTIVO'),
(4, 'Patricia Elena', 'Campos Diaz', '46985231', 'AIIIB-4698', '932145678', 'INACTIVO'),
(5, 'Miguel Angel', 'Vargas Flores', '48321654', 'AIIIC-4832', '998741256', 'ACTIVO');

INSERT INTO vehiculos (id_vehiculo, placa, marca, modelo, estado) VALUES
(1, 'ABC-123', 'Hyundai', 'H100', 'ASIGNADO'),
(2, 'F4T-892', 'Toyota', 'Hilux', 'ASIGNADO'),
(3, 'B7L-456', 'Foton', 'Aumark', 'DISPONIBLE'),
(4, 'C9P-741', 'JAC', 'Sunray', 'DISPONIBLE'),
(5, 'D2K-305', 'Nissan', 'Urban', 'MANTENIMIENTO'),
(6, 'E8R-219', 'Mercedes-Benz', 'Sprinter', 'INACTIVO');

INSERT INTO rutas (id_ruta, origen, destino, fecha_programada, estado) VALUES
(1, 'Centro de Distribucion Lima Este', 'Tienda Retail Jockey Plaza', '2026-06-12', 'EN_CURSO'),
(2, 'Centro de Distribucion Lima Norte', 'Tienda Retail Mega Plaza', '2026-06-13', 'PROGRAMADA'),
(3, 'Centro de Distribucion Villa El Salvador', 'Tienda Retail Mall del Sur', '2026-06-10', 'FINALIZADA'),
(4, 'Centro de Distribucion Callao', 'Tienda Retail Plaza San Miguel', '2026-06-11', 'CANCELADA'),
(5, 'Centro de Distribucion Ate', 'Tienda Retail Real Plaza Puruchuco', '2026-06-14', 'PROGRAMADA'),
(6, 'Centro de Distribucion Lurin', 'Tienda Retail Open Plaza Angamos', '2026-06-15', 'PROGRAMADA');

INSERT INTO asignaciones_ruta (id_asignacion, id_ruta, id_conductor, id_vehiculo, fecha_asignacion, estado) VALUES
(1, 1, 1, 1, '2026-06-12', 'EN_CURSO'),
(2, 2, 2, 2, '2026-06-12', 'PROGRAMADA'),
(3, 3, 3, 3, '2026-06-09', 'FINALIZADA'),
(4, 4, 5, 4, '2026-06-10', 'CANCELADA');

INSERT INTO recorridos_gps (id_gps, id_asignacion, latitud, longitud, fecha_hora) VALUES
(1, 1, -12.04318000, -76.93512000, '2026-06-12 08:15:00.000000'),
(2, 1, -12.05234000, -76.94876000, '2026-06-12 08:35:00.000000'),
(3, 1, -12.06645000, -76.96421000, '2026-06-12 08:55:00.000000'),
(4, 3, -12.21487000, -76.93754000, '2026-06-10 09:20:00.000000'),
(5, 3, -12.19936000, -76.94681000, '2026-06-10 09:50:00.000000'),
(6, 3, -12.18477000, -76.95564000, '2026-06-10 10:15:00.000000');

INSERT INTO incidencias (id_incidencia, id_asignacion, tipo, descripcion, fecha_hora, estado) VALUES
(1, 1, 'TRAFICO', 'Congestion vehicular en ruta principal. Se registra demora estimada de 20 minutos.', '2026-06-12 08:42:00.000000', 'PENDIENTE'),
(2, 3, 'ENTREGA', 'Entrega completada con observacion de recepcion parcial.', '2026-06-10 10:30:00.000000', 'RESUELTA'),
(3, 4, 'OPERATIVA', 'Ruta cancelada por reprogramacion de tienda destino.', '2026-06-11 07:45:00.000000', 'EN_REVISION');

