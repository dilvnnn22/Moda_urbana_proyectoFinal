CREATE TABLE pagos (

                       id BIGINT PRIMARY KEY AUTO_INCREMENT,

                       pedido_id BIGINT,

                       monto DOUBLE,

                       metodo_pago VARCHAR(100),

                       estado VARCHAR(100)
);