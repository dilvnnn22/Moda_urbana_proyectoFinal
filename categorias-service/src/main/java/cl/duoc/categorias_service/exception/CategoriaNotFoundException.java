package cl.duoc.categorias_service.exception;

public class CategoriaNotFoundException extends RuntimeException {
    public CategoriaNotFoundException(Long id) {
        super("Categoria con id " + id + " no encontrado/a");
    }
}
