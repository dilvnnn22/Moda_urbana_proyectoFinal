package cl.duoc.clientes_service.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(Long id) {
        super("Cliente con id " + id + " no encontrado/a");
    }
}
