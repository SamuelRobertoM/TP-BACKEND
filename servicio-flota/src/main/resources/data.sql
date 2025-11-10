-- ============================================
-- DATOS DE PRUEBA PARA SERVICIO-FLOTA
-- ============================================

-- ============================================
-- CAMIONES
-- ============================================
-- Camión 1: Disponible, capacidad grande
INSERT INTO camion (dominio, nombre_transportista, telefono, capacidad_peso, capacidad_volumen, consumo_combustible_por_km, disponible, costo_por_km) 
VALUES ('AB123CD', 'Transporte López SRL', '+54 351 4567890', 15000.0, 80.0, 0.35, true, 150.0);

-- Camión 2: NO Disponible (en uso), capacidad mediana
INSERT INTO camion (dominio, nombre_transportista, telefono, capacidad_peso, capacidad_volumen, consumo_combustible_por_km, disponible, costo_por_km) 
VALUES ('EF456GH', 'Logística del Sur SA', '+54 341 7654321', 10000.0, 60.0, 0.28, false, 120.0);

-- Camión 3: Disponible, capacidad pequeña
INSERT INTO camion (dominio, nombre_transportista, telefono, capacidad_peso, capacidad_volumen, consumo_combustible_por_km, disponible, costo_por_km) 
VALUES ('IJ789KL', 'Expreso Córdoba', '+54 351 9876543', 5000.0, 35.0, 0.22, true, 90.0);

-- ============================================
-- DEPÓSITOS
-- ============================================
-- Depósito 1: Córdoba Capital
INSERT INTO deposito (nombre, direccion, latitud, longitud) 
VALUES ('Depósito Central Córdoba', 'Av. Circunvalación 1500, Córdoba, Argentina', -31.4201, -64.1888);

-- Depósito 2: Rosario
INSERT INTO deposito (nombre, direccion, latitud, longitud) 
VALUES ('Depósito Rosario Norte', 'Ruta Nacional 9 Km 305, Rosario, Santa Fe, Argentina', -32.9442, -60.6505);

-- ============================================
-- TARIFAS
-- ============================================
-- Tarifa 1: Activa - Vigente desde 2020 (fecha antigua para testing genérico)
INSERT INTO tarifas (costo_km_base, precio_litro_combustible, cargo_gestion_por_tramo, costo_estadia_diaria, vigencia_desde, vigencia_hasta, activa) 
VALUES (100.0, 850.0, 5000.0, 2500.0, '2020-01-01 00:00:00', NULL, true);

-- Tarifa 2: Inactiva - Tarifa antigua (ejemplo histórico)
INSERT INTO tarifas (costo_km_base, precio_litro_combustible, cargo_gestion_por_tramo, costo_estadia_diaria, vigencia_desde, vigencia_hasta, activa) 
VALUES (80.0, 700.0, 4000.0, 2000.0, '2019-01-01 00:00:00', '2019-12-31 23:59:59', false);
