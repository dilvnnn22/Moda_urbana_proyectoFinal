package cl.duoc.carrito_service.repository;



import cl.duoc.carrito_service.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
}
