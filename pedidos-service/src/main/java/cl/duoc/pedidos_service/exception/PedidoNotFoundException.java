package cl.duoc.pedidos_service.exception;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long id) {
        super("Pedido con id " + id + " no encontrado/a");
    }
}
