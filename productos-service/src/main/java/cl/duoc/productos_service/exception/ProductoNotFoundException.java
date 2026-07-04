package cl.duoc.productos_service.exception;

public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String id) {
        super("Producto con id " + id + " no encontrado/a");
    }
}
