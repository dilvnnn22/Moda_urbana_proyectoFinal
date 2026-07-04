package cl.duoc.carrito_service.exception;

public class CarritoNotFoundException extends RuntimeException {
    public CarritoNotFoundException(Long id) {
        super("Carrito con id " + id + " no encontrado/a");
    }
}
