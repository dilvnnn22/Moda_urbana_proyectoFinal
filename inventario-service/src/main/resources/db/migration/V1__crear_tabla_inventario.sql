CREATE TABLE inventario (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            producto_id BIGINT,
                            cantidad_disponible INT,
                            ubicacion_bodega VARCHAR(100)
);