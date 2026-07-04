CREATE TABLE productos (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           nombre VARCHAR(100),
                           descripcion VARCHAR(255),
                           precio DOUBLE,
                           stock INT,
                           marca_id BIGINT,
                           categoria_id BIGINT
);