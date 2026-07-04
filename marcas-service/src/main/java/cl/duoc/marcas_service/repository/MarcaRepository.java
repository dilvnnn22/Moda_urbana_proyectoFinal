package cl.duoc.marcas_service.repository;



import cl.duoc.marcas_service.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
}
