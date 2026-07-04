package cl.duoc.marcas_service.exception;

public class MarcaNotFoundException extends RuntimeException {
    public MarcaNotFoundException(Long id) {
        super("Marca con id " + id + " no encontrado/a");
    }
}
