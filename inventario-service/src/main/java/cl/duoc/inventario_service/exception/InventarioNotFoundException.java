package cl.duoc.inventario_service.exception;

public class InventarioNotFoundException extends RuntimeException {
    public InventarioNotFoundException(Long id) {
        super("Inventario con id " + id + " no encontrado/a");
    }
}
