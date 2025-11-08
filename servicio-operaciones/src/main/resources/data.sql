-- ============================================
-- DATOS DE PRUEBA PARA SERVICIO-OPERACIONES
-- ============================================

-- ============================================
-- CLIENTES
-- ============================================
INSERT INTO cliente (nombre, email, telefono, direccion, cuit) 
VALUES ('Juan Perez', 'juan.perez@email.com', '351-1234567', 'Calle Falsa 123, Córdoba', '20-12345678-9');

INSERT INTO cliente (nombre, direccion, telefono, email, cuit) 
VALUES ('Empresa Logística SA', 'Av. Colón 1500, Córdoba, Argentina', '+54 351 4567890', 'contacto@logistica.com', '30-98765432-1');

INSERT INTO cliente (nombre, direccion, telefono, email, cuit) 
VALUES ('Transportes del Sur SRL', 'Calle San Martín 234, Rosario, Santa Fe', '+54 341 7654321', 'ventas@transportessur.com', '33-55667788-9');

-- ============================================
-- CONTENEDORES
-- ============================================
-- Contenedor del cliente Juan Perez (ID 1)
INSERT INTO contenedor (numero, tipo, peso, volumen, estado, cliente_id) 
VALUES ('CONT-JP-001', 'STANDARD', 1500.0, 33.0, 'EN_ORIGEN', 1);

-- Contenedores de Empresa Logística SA (ID 2)
INSERT INTO contenedor (numero, tipo, peso, volumen, estado, cliente_id) 
VALUES ('CONT-001', 'STANDARD', 5000.0, 25.0, 'EN_ORIGEN', 2);

INSERT INTO contenedor (numero, tipo, peso, volumen, estado, cliente_id) 
VALUES ('CONT-002', 'REFRIGERADO', 3500.0, 18.0, 'EN_DEPOSITO', 2);

-- Contenedor de Transportes del Sur (ID 3)
INSERT INTO contenedor (numero, tipo, peso, volumen, estado, cliente_id) 
VALUES ('CONT-003', 'STANDARD', 7500.0, 40.0, 'EN_ORIGEN', 3);

-- ============================================
-- RUTAS
-- ============================================
-- Ruta 1: Córdoba a Buenos Aires
INSERT INTO ruta (origen, destino, latitud_origen, longitud_origen, latitud_destino, longitud_destino, distancia_km, tiempo_estimado_horas) 
VALUES ('Córdoba, Argentina', 'Buenos Aires, Argentina', -31.4201, -64.1888, -34.6037, -58.3816, 0, 0);

-- Ruta 2: Rosario a Mendoza
INSERT INTO ruta (origen, destino, latitud_origen, longitud_origen, latitud_destino, longitud_destino, distancia_km, tiempo_estimado_horas) 
VALUES ('Rosario, Santa Fe', 'Mendoza, Argentina', -32.9442, -60.6505, -32.8895, -68.8458, 0, 0);

-- ============================================
-- SOLICITUDES
-- ============================================
-- Solicitud 1: Cliente Juan Perez (BORRADOR - sin ruta asignada)
INSERT INTO solicitud (contenedor_id, cliente_id, ruta_id, fecha_solicitud, estado, observaciones, costo_estimado, tiempo_estimado, costo_final, tiempo_real) 
VALUES (1, 1, NULL, '2025-10-20', 'BORRADOR', 'Entrega urgente.', 0, 0, 0, 0);

-- Solicitud 2: Empresa Logística SA (PENDIENTE - con ruta asignada)
INSERT INTO solicitud (contenedor_id, cliente_id, ruta_id, fecha_solicitud, estado, observaciones, costo_estimado, tiempo_estimado, costo_final, tiempo_real) 
VALUES (2, 2, 1, '2025-10-20', 'PENDIENTE', 'Transporte estándar a Buenos Aires', 0, 0, 0, 0);

-- Solicitud 3: Transportes del Sur (PENDIENTE - con ruta asignada)
INSERT INTO solicitud (contenedor_id, cliente_id, ruta_id, fecha_solicitud, estado, observaciones, costo_estimado, tiempo_estimado, costo_final, tiempo_real) 
VALUES (4, 3, 2, '2025-10-21', 'PENDIENTE', 'Carga pesada a Mendoza', 0, 0, 0, 0);
